package system.stellar_stay.modules.properties.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.properties.enums.SalaryType;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sub_managers")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class SubManagerEntity{

    @Id
    @Column(name = "sub_manager_id")
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "sub_manager_id")
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private PropertiesEntity property;

    @Column(name = "salary_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @Column(name = "salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal salary;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedAt;

    @Column(name = "is_working", nullable = false)
    private boolean is_working;

    @Column(name = "leaved_date")
    private LocalDate leavedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subManager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffEntity> staffs;
}
