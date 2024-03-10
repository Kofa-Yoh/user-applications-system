package com.kotkina.userapplicationssystem.securily.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotkina.userapplicationssystem.web.models.response.AuthErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        AuthErrorResponse body = new AuthErrorResponse();
        body.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        body.setError(authException.getMessage());
        body.setMessage("У вас нет прав на выполнение данного запроса.");
        body.setPath(request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
