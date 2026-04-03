package system.stellar_stay.modules.identify.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "account_role")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(Account_Role.Account_Role_Id.class)
@Setter
@Getter
public class Account_Role {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Account_Role_Id implements Serializable {
        private UUID account;
        private UUID role;
    }


}
