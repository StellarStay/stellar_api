package system.stellar_stay.modules.identify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "role_permission")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(Role_Permission.Role_Permission_Id.class)
@Getter
@Setter
public class Role_Permission {


    @Id
    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Role_Permission_Id implements Serializable {
        private UUID role;
        private UUID permission;
    }
}
