package org.example.springstarterproject.service.implementation;

import jakarta.transaction.Transactional;
import org.example.springstarterproject.model.BlacklistedToken;
import org.example.springstarterproject.repository.TokenBlacklistRepository;
import org.example.springstarterproject.service.TokenBlacklistService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenBlacklistServiceImp implements TokenBlacklistService {

    private final JwtDecoder jwtDecoder;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenBlacklistServiceImp(JwtDecoder jwtDecoder, TokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtDecoder = jwtDecoder;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Transactional
    @Override
    public void blacklistToken(String token) {

        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiry = jwt.getExpiresAt();

            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiryDate(expiry != null ? expiry : Instant.now());

            tokenBlacklistRepository.save(blacklistedToken);
        } catch (
                JwtException e) {
            // Token is already invalid or expired, no need to blacklist
        }
    }

    @Override
    public boolean checkToken(String token) {
        BlacklistedToken foundToken = tokenBlacklistRepository.findByToken(token);

        return foundToken != null;
    }

}
