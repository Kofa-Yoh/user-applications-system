package com.kotkina.userapplicationssystem.securily;

import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.exceptions.DataNotFoundException;
import com.kotkina.userapplicationssystem.repositories.UserRepository;
import com.kotkina.userapplicationssystem.securily.jwt.JwtUtils;
import com.kotkina.userapplicationssystem.services.TokenBlacklistService;
import com.kotkina.userapplicationssystem.services.UserService;
import com.kotkina.userapplicationssystem.web.models.request.AuthRequest;
import com.kotkina.userapplicationssystem.web.models.response.AuthResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final UserService userService;

    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        if (authRequest == null) {
            throw new DataNotFoundException("Пользователь с указанным телефоном не найден.");
        }

        User user = userService.getUserByVerifiedPhone(authRequest.getPhone());

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                authRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return AuthResponse.builder()
                .token(jwtUtils.generateJwtToken(userDetails))
                .roles(roles)
                .build();
    }

    public void logout(HttpServletRequest request) {
        String jwtToken = getToken(request);

        if (jwtToken != null && jwtUtils.validate(jwtToken)) {
            addTokenInBlacklist(jwtToken);
        }

        SecurityContextHolder.clearContext();
    }

    public UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (UserDetailsImpl) authentication.getPrincipal();
        } else {
            return null;
        }
    }

    private String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private void addTokenInBlacklist(String token) {
        if (token != null) {
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                User user = userRepository.findUserByUsername(username)
                        .orElse(null);
                if (user == null) return;

                LocalDateTime expirationDate = jwtUtils.getExpirationFromToken(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                Long seconds = Duration.between(LocalDateTime.now(), expirationDate).getSeconds();

                tokenBlacklistService.addTokenInBlacklist(token, LocalDateTime.now(), seconds);

            } catch (ExpiredJwtException e) {
                Logger.getLogger(this.getClass().getSimpleName()).warning(e.getMessage());
            }
        }
    }
}
