package com.example.oms.security.refresh;

import com.example.oms.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 7;

    private final RefreshTokenRepository repository;

    public RefreshToken createRefreshToken(UserEntity user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS)
        );

        return repository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public RefreshToken getByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    public RefreshToken verifyAndRotate(String tokenValue) {
        RefreshToken oldToken = repository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (oldToken.isRevoked()) {
            // Token reuse detected → revoke everything
            repository.revokeAllByUser(oldToken.getUser());
            throw new RuntimeException("Refresh token reuse detected. All sessions revoked.");
        }

        if (oldToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Invalidate old token
        oldToken.setRevoked(true);

        // Create new refresh token
        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setUser(oldToken.getUser());
        newToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        return repository.save(newToken);
    }
}

