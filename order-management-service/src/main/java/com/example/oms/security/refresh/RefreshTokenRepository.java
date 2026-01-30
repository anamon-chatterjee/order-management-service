package com.example.oms.security.refresh;

import com.example.oms.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(UserEntity user);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user = :user")
    void revokeAllByUser(@Param("user") UserEntity user);

    void deleteByExpiryDateBefore(Instant now);

    @Modifying
    @Query("""
        delete from RefreshToken rt
        where rt.revoked = true
          and rt.expiryDate < :cutoff
    """)
    void deleteRevokedAndExpired(@Param("cutoff") Instant cutoff);
}
