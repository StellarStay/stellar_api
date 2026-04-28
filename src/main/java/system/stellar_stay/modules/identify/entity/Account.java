package system.stellar_stay.modules.identify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
import system.stellar_stay.modules.properties.entity.StaffEntity;
import system.stellar_stay.modules.properties.entity.SubManagerEntity;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.util.List;

@Entity
@Table(name = "accounts")
@Setter
@Getter
@AllArgsConstructor 
@NoArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OTPCode> otpCodes;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PropertiesEntity> properties;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    // cascade = CascadeType.ALL để tự động xóa SubManagerEntity khi Account bị xóa
    // orphanRemoval = true để đảm bảo rằng nếu SubManagerEntity không còn liên kết với Account nào thì nó sẽ bị xóa khỏi cơ sở dữ liệu
    private SubManagerEntity subManager;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private StaffEntity staff;
}
