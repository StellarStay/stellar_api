package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.properties.enums.RoomStatus;
import system.stellar_stay.modules.properties.enums.RoomType;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoomsEntity extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "room_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "description")
    private String description;

    @Column(name = "max_occupancy", nullable = false)
    private int max_occupancy; // số người tối đa/phòng

    @Column(name =  "floor", nullable = false)
    private int floor;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "base_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal base_price;

    @Column(name = "currency", nullable = false)
    private String currency; // đơn vị tiền tệ của giá phòng, ví dụ: USD, EUR, VND

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @Column(name = "is_available", nullable = false)
    private boolean is_available; // trạng thái phòng có sẵn để đặt hay không

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertiesEntity property;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImageEntity> images;
}
