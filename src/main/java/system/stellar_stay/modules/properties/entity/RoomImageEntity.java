package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.properties.enums.MediaType;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

@Entity
@Table(name = "room_images")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoomImageEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomsEntity room;

    @Column(name = "url", nullable = false)
    private String url; // lưu url trên S3

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder; // thứ tự hiển thị ảnh trong phòng

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary; // thumbnail chính của phòng



}
