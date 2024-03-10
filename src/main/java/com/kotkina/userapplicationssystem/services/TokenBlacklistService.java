package com.kotkina.userapplicationssystem.services;

import com.kotkina.userapplicationssystem.entities.TokenBlacklist;
import com.kotkina.userapplicationssystem.repositories.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public boolean isTokenInBlacklist(String token, LocalDateTime now) {
        TokenBlacklist tokenInBlacklist = tokenBlacklistRepository.findTokenBlacklistByToken(token);
        if (tokenInBlacklist == null) {
            return false;
        } else {
            // токен просрочен, удаляем
            if (tokenInBlacklist.getCreateDateTime().plusSeconds(tokenInBlacklist.getExpiration()).isBefore(now)) {
                tokenBlacklistRepository.delete(tokenInBlacklist);
            }
            return true;
        }
    }

    public void addTokenInBlacklist(String token, LocalDateTime now, Long expiration) {
        TokenBlacklist tokenInBlacklist = tokenBlacklistRepository.findTokenBlacklistByToken(token);
        if (tokenInBlacklist == null) {
            TokenBlacklist tokenBlacklist = new TokenBlacklist();
            tokenBlacklist.setToken(token);
            tokenBlacklist.setCreateDateTime(now);
            tokenBlacklist.setExpiration(expiration);
            tokenBlacklistRepository.save(tokenBlacklist);
        }
    }
}
