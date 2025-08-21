package tech.task.dataox.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class OrderDto {

    Long orderId;

    @Schema(example = "Book about the cat")
    String title;

    @Schema(example = "1")
    Long supplierId;

    @Schema(example = "2")
    Long consumerId;

    @Schema(example = "100.00")
    BigDecimal price;

    @Schema(example = "2025-08-18T14:32:45.183")
    LocalDateTime startProcessingAt;

    @Schema(example = "2025-08-18T14:32:48.223")
    LocalDateTime endProcessingAt;

    @Schema(example = "2025-08-18T14:33:02.296")
    LocalDateTime savedAt;
}
