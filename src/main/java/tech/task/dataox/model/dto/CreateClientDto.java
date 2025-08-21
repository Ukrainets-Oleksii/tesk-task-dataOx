package tech.task.dataox.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateClientDto {

    @NotBlank
    @Size(max = 100)
    @Schema(example = "John")
    String name;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Mask")
    String lastName;

    @NotBlank(message = "Email should be valid and not blank")
    @Size(max = 50)
    @Email(regexp = ".+@.+\\..+", message = "Email should be valid")
    @Schema(example = "example@gmail.com")
    String email;
}