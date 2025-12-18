package org.example.springstarterproject.service.implementation;

import org.example.springstarterproject.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenServiceImp implements TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenServiceImp(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, 15, ChronoUnit.MINUTES, true);
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, 5, ChronoUnit.DAYS, false);
    }

    private String generateToken(Authentication authentication, int amount, ChronoUnit unit, boolean includeRoles) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claimsSetBuilder = JwtClaimsSet.builder()
                .issuer("issuer")
                .issuedAt(now)
                .expiresAt(now.plus(amount, unit))
                .subject(authentication.getName());

        if (includeRoles) {
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            claimsSetBuilder.claim("scope", scope);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSetBuilder.build())).getTokenValue();
    }
}
