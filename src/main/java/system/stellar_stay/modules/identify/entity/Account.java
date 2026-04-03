package system.stellar_stay.modules.identify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.identify.enums.AccountStatus;
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

}
