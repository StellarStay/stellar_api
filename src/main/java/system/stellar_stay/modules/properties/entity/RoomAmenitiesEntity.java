package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "room_amenities")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RoomAmenitiesEntity.RoomAmenitiesId.class)
public class RoomAmenitiesEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomsEntity room;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "amenities_id", nullable = false)
    private AmenitiesEntity amenities;


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RoomAmenitiesId implements Serializable {
        private RoomsEntity room;
        private AmenitiesEntity amenities;
    }

}
