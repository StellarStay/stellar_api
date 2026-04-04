package system.stellar_stay.modules.identify.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.identify.enums.OTPStatus;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_code")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class OTPCode extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = true)
    private Account account;

    @Column(name = "email_verified", nullable = false)
    private String emailVerified;

    @Column(name = "otp_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OTPStatus status;

    @Column(name = "otp_hashed", nullable = false)
    private String otpHashed;

    @Column(name =  "expired_at", nullable = false)
    private LocalDateTime expiredAt;

}
