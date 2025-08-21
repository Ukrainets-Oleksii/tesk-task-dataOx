package tech.task.dataox.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import tech.task.dataox.lib.NullOrNotBlank;

@Value
@Builder
public class UpdateClientDto {

    @NullOrNotBlank
    @Size(max = 100)
    @Schema(example = "John")
    String name;

    @NullOrNotBlank
    @Size(max = 100)
    @Schema(example = "Mask")
    String lastName;

    @NullOrNotBlank
    @Size(max = 50)
    @Email(message = "Email should be valid")
    @Schema(example = "example@gmail.com")
    String email;

    @NullOrNotBlank
    @Size(max = 250)
    @Schema(example = "123 Main St, NY")
    String address;

    @NullOrNotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be a valid international number")
    @Schema(example = "+380501234567")
    String phone;
}
