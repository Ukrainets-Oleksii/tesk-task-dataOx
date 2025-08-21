package tech.task.dataox.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.task.dataox.model.dto.ClientDto;
import tech.task.dataox.model.dto.CreateClientDto;
import tech.task.dataox.model.dto.UpdateClientDto;
import tech.task.dataox.service.ClientService;
import tech.task.dataox.service.mapper.ClientMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/clients")
@Validated
@RequiredArgsConstructor
@Tag(name = "Clients", description = "CRUD and search operations for clients")
public class ClientController {
    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @Operation(summary = "Create client",
            description = "Creates a new client and returns created entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Email or phone already in use")
    })
    @RequestBody(
        required = true,
        description = "Client payload",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateClientDto.class),
            examples = @ExampleObject(
                value = """
                {
                  "name": "John",
                  "lastName": "Mask",
                  "email": "example@gmail.com"
                }
                """
            )
        )
    )
    @PostMapping
    public ResponseEntity<ClientDto> createClient(
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateClientDto dto) {
        ClientDto client = clientMapper.toDto(clientService.createClient(clientMapper.fromCreateDto(dto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @Operation(summary = "Get client by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "404", description = "Client not found or inactive")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@Parameter(description = "Client id") @PathVariable Long id) {
        return ResponseEntity.ok(clientMapper.toDto(clientService.findClientById(id)));
    }

    @Operation(
            summary = "Get client profit by id",
            description = "Returns the total profit of a client across all orders."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client profit",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "number", example = "199.99")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found or inactive"
            )
    })
    @GetMapping("/profit/{id}")
    public ResponseEntity<BigDecimal> getProfit(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findClientProfitById(id));
    }

    @Operation(
            summary = "Search clients by profit range",
            description = "Returns a paginated list of clients whose profit value " +
                    "is between the given min and max range."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved clients",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/by-profit")
    public ResponseEntity<Page<ClientDto>> getClientsByProfit(
            @Parameter(description = "Minimum profit value", example = "1000.00") @RequestParam BigDecimal min,
            @Parameter(description = "Maximum profit value", example = "5000.00") @RequestParam BigDecimal max,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                clientService.findClientsByProfitBetween(pageable, min, max)
                        .map(clientMapper::toDto));
    }

    @Operation(summary = "Search clients",
            description = "Search by keyword across name/lastName/email/address (min 3 characters). Supports pagination.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page of clients",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<Page<ClientDto>> getClients(
            @Parameter(description = "Search keyword (min 3)") @RequestParam @Size(min = 3) String q,
            Pageable pageable) {
        Page<ClientDto> result = clientService.findClientsByKeyword(q, pageable)
                .map(clientMapper::toDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update client",
            description = "Partial update of client by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client updated",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Client not found or inactive"),
        @ApiResponse(responseCode = "409", description = "Email or phone already in use")
    })
    @RequestBody(
        required = true,
        description = "Update payload",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UpdateClientDto.class),
            examples = @ExampleObject(
                value = """
                {
                  "name": "John",
                  "lastName": "Mask",
                  "email": "example@gmail.com",
                  "address": "123 Main St, NY",
                  "phone": "+380501234567"
                }
                """
            )
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody UpdateClientDto dto) {
        return ResponseEntity.ok(clientMapper.toDto(clientService.update(id, dto)));
    }

    @Operation(summary = "Recover client",
            description = "Activates previously deactivated client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recovered"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PatchMapping("/recover/{id}")
    public ResponseEntity<Void> recoverClient(@PathVariable Long id) {
        clientService.recoverClientById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soft delete client",
            description = "Marks client as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deactivated"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateClientById(@PathVariable Long id) {
        clientService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reset profit of all clients",
            description = "This endpoint sets the profit of all clients to 0. "
                    + "Orders are not deleted or modified; only the profit field is reset."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profit successfully reset for all clients"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reset-profit")
    public ResponseEntity<Void> resetProfit() {
        clientService.resetAllProfit();
        return ResponseEntity.noContent().build();
    }
}
