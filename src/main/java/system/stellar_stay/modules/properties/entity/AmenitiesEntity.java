package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.properties.enums.AmenitiesType;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

@Entity
@Table(name = "amenities")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AmenitiesEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String iconName;

    @Column(name = "description")
    private String description;

    @Column(name = "amenities_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AmenitiesType amenitiesType;


}
