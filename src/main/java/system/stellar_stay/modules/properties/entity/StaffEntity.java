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
import system.stellar_stay.modules.properties.enums.StaffPosition;
import system.stellar_stay.shared.infrastructure.persistence.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staffs")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class StaffEntity {

    @Id
    @Column(name = "staff_id")
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "staff_id", nullable = false, unique = true)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertiesEntity property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_manager_id", nullable = false)
    private SubManagerEntity subManager;

    @Column(name = "position", nullable = false)
    @Enumerated(EnumType.STRING)
    private StaffPosition position;


    @Column(name = "salary_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @Column(name = "salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal salary;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedAt;

    @Column(name = "is_working", nullable = false)
    private boolean isWorking;

    @Column(name = "leaved_date")
    private LocalDate leavedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
