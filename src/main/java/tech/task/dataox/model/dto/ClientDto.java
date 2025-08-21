package tech.task.dataox.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class ClientDto {

    Long id;

    @Schema(example = "John")
    String name;

    @Schema(example = "Doe")
    String lastName;

    @Schema(example = "john.doe@example.com")
    String email;

    @Schema(example = "123 Main St, NY")
    String address;

    @Schema(example = "+380501234567")
    String phone;

    @Schema(example = "true")
    boolean isActive;

    @Schema(example = "100.00")
    BigDecimal profit;

    @Schema(example = "2025-08-18T14:37:32.206")
    LocalDateTime createdAt;
}
