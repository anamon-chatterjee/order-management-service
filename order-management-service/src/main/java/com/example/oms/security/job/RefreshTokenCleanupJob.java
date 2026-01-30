package com.example.oms.security.job;

import com.example.oms.security.refresh.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();

        log.info("Starting refresh token cleanup job");

        Instant cutoff = now.minus(1, ChronoUnit.DAYS);

        refreshTokenRepository.deleteByExpiryDateBefore(cutoff);

        log.info("Completed refresh token cleanup job");
    }
}

