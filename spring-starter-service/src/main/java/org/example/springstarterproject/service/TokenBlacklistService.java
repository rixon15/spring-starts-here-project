package org.example.springstarterproject.service;

import jakarta.transaction.Transactional;

public interface TokenBlacklistService {
    @Transactional
    void blacklistToken(String token);

    boolean checkToken(String token);
}
