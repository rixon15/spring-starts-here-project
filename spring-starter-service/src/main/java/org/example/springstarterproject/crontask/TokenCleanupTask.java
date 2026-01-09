package org.example.springstarterproject.crontask;

import org.example.springstarterproject.repository.TokenBlacklistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class TokenCleanupTask {
    private final TokenBlacklistRepository repository;

    public TokenCleanupTask(TokenBlacklistRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void removeExpiredBlacklistedTokens() {
        repository.deleteByExpiryDateBefore(Instant.now());
    }
}
