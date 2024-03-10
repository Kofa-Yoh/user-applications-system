package com.kotkina.userapplicationssystem.repositories;

import com.kotkina.userapplicationssystem.entities.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, Long> {

    TokenBlacklist findTokenBlacklistByToken(String token);
}
