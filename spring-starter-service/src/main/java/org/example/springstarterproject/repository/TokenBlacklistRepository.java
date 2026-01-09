package org.example.springstarterproject.repository;

import org.example.springstarterproject.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<BlacklistedToken, Long> {

    void deleteByExpiryDateBefore(Instant now);

    BlacklistedToken findByToken(String token);
}
