package system.stellar_stay.modules.identify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Profile {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "id_card_number", nullable = false, unique = true)
    private String IdCardNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "avatar_url", nullable = true)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private GenderEnum gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints;
}
