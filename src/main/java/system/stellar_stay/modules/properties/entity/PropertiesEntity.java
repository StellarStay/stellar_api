package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;
import system.stellar_stay.modules.properties.enums.PropertiesType;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "properties")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PropertiesEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // unique identifier for the property, often used in URLs

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertiesType type;

    @Column(name = "description")
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "ward", nullable = false)
    private String ward;

    @Column(name = "latitude", precision = 10, scale = 7) // precision là tổng số chữ số, scale là số chữ số sau dấu thập phân
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertiesStatus status;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
    // field này dùng để manager cho phép hiển thị lên UI hay không
    // Tức là kiểu status là để xem cái property này đang ở trạng thái nào trong hệ thống
    // Còn cái is_available này để quản lý việc hiển thị lên UI hay không đối với khách hàng.
    // ex: status: ACTIVE, is_available: false -> property vẫn hoạt động nhưng khách search méo được do manager bảo trì hay gì đó

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Account account;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private SubManagerEntity subManager;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffEntity> staffs;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomsEntity> rooms;
}
