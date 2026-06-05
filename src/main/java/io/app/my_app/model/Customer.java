package io.app.my_app.model;

import io.app.my_app.audits.InitiatorAudit;
import io.app.my_app.model.enums.UtilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_national_id", columnList = "national_id"),
        @Index(name = "idx_customer_email", columnList = "email")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_names", nullable = false, length = 255)
    private String fullNames;

    @Column(name = "national_id", nullable = false, unique = true, length = 50)
    private String nationalId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "address", length = 1000)
    private String address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UtilityStatus status = UtilityStatus.ACTIVE;
}
