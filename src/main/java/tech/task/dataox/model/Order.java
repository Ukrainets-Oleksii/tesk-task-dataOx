package tech.task.dataox.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "orders",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "supplier_id", "consumer_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(nullable = false)
    String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    Client supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    Client consumer;

    @NotNull
    @Positive
    @Column(nullable = false)
    BigDecimal price;

    @Version
    private Timestamp version;

    @Column(nullable = false)
    LocalDateTime startProcessingAt;

    @Column(nullable = false)
    LocalDateTime endProcessingAt;

    @CreationTimestamp
    LocalDateTime savedAt;

    @Builder.Default
    @Column(nullable = false)
    boolean isActive = true;
}
