package com.kotkina.userapplicationssystem.web.controllers;

import com.kotkina.userapplicationssystem.securily.SecurityService;
import com.kotkina.userapplicationssystem.web.models.request.AuthRequest;
import com.kotkina.userapplicationssystem.web.models.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(authRequest));
    }

    @PostMapping("/user_logout")
    public ResponseEntity<String> logoutUser(@AuthenticationPrincipal UserDetails userDetails,
                                             HttpServletRequest request) {
        securityService.logout(request);

        return ResponseEntity.ok("Выход.");
    }
}
