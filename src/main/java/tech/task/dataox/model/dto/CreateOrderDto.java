package tech.task.dataox.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CreateOrderDto {

    @NotBlank(message = "Title can not be blank or shorter the 3 symbols")
    @Size(min = 3, max = 200)
    @Schema(example = "Book about the cat")
    String title;

    @NotNull(message = "Supplier is can not be null")
    @Schema(example = "1")
    Long supplierId;

    @NotNull(message = "Consumer is can not be null")
    @Schema(example = "2")
    Long consumerId;

    @Positive(message = "Price below 0 or equal 0")
    @Schema(example = "199.99")
    BigDecimal price;
}
