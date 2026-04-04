package system.stellar_stay.modules.identify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name =  "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "ip_address", nullable = false)
    private String IPAddress;

    @Column(name =  "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked;
}
