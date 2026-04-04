package system.stellar_stay.modules.identify.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "account_role")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AccountRole.AccountRoleId.class)
@Setter
@Getter
public class AccountRole {

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
    public static class AccountRoleId implements Serializable {
        private Account account;
        private Role role;
    }


}
