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
@Table(name = "property_images")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PropertyImageEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertiesEntity property;

    @Column(name = "url", nullable = false)
    private String url; // lưu url trên S3

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder; // thứ tự hiển thị ảnh trong property

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary; // thumbnail chính của property

}
