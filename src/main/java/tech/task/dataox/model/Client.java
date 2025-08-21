package tech.task.dataox.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(nullable = false)
    String name;

    @NotBlank
    @Column(nullable = false)
    String lastName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    String email;

    String address;

    @Column(unique = true)
    String phone;

    @Column(nullable = false)
    @Builder.Default
    boolean isActive = true;

    LocalDateTime inactiveAt;

    @Version
    private Timestamp version;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> suppliedOrders;

    @OneToMany(mappedBy = "consumer", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> consumedOrders;

    @Column(nullable = false)
    @Builder.Default
    BigDecimal profit = BigDecimal.ZERO;

    @CreationTimestamp
    LocalDateTime createdAt;
}
