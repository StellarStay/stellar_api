package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import system.stellar_stay.modules.identify.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    RefreshToken findByRefreshToken(String refreshToken);

    @Query(
            """
            SELECT rt.refreshToken
            FROM RefreshToken rt
            WHERE rt.account.id = :accountId AND rt.isRevoked = false
            """
    )
    List<String> findAllByAccountIdAndRevokedFalse(UUID accountId);


    @Modifying
    @Transactional
    @Query(
            """
                delete from RefreshToken rt
                where rt.expiredAt < :threshold
            """
    )
    int deleteByExpiredAtBefore(@Param("threshold") LocalDateTime threshold);
}
