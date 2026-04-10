package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import system.stellar_stay.modules.identify.entity.OTPCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OTPRepository extends JpaRepository<OTPCode, UUID> {

    @Modifying // Đối với những truy vấn thay đổi dữ liệu (INSERT, UPDATE, DELETE), cần thêm @Modifying
    @Transactional
    @Query("""
        delete from OTPCode  otp
        where otp.expiredAt < :threshold
    """)
    int deleteByExpiredAtBefore(LocalDateTime threshold);


    @Modifying
    @Transactional
    @Query("""
        delete from OTPCode otp
        where otp.emailVerified = :email and otp.status = :status
        """)
    void deleteByEmailAndStatus(String email, String status);

}
