package tech.task.dataox.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tech.task.dataox.model.dto.CreateOrderDto;
import tech.task.dataox.model.dto.OrderDto;
import tech.task.dataox.service.OrderService;
import tech.task.dataox.service.mapper.OrderMapper;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Create a new order",
            description = "Creates a new order for a client, supplier, and consumer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Client, supplier or consumer not found", content = @Content(mediaType = "application/json"))
    })
    @RequestBody(description = "Order creation request", required = true,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateOrderDto.class),
            examples = @ExampleObject(
                value = """
                {
                  "title": "Book about the cat",
                  "supplierId": 2,
                  "consumerId": 3,
                  "price": 100.50
                }
                """)))
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @org.springframework.web.bind.annotation.RequestBody CreateOrderDto dto) {
        OrderDto order = orderMapper.toDto(orderService.create(orderMapper.toEntity(dto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(summary = "Get order by ID",
            description = "Retrieve order details by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "id", description = "Order ID", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderMapper.toDto(orderService.findById(id)));
    }

    @Operation(summary = "Get orders by client ID",
            description = "Retrieve all orders associated with a specific client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found"),
        @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "clientId", description = "Client ID", required = true)
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderDto>> getOrderByClientId(@PathVariable Long clientId) {
        List<OrderDto> orders = orderService.findAllByClientId(clientId).stream()
                .map(orderMapper::toDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get orders by supplier ID",
            description = "Retrieve all orders associated with a specific supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found"),
        @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "supplierId", description = "Supplier ID", required = true)
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<OrderDto>> getBySupplierId(@PathVariable Long supplierId) {
        List<OrderDto> orders = orderService.findBySupplierId(supplierId).stream()
                .map(orderMapper::toDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get orders by consumer ID",
            description = "Retrieve all orders associated with a specific consumer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found"),
        @ApiResponse(responseCode = "404", description = "Consumer not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "consumerId", description = "Consumer ID", required = true)
    @GetMapping("/consumer/{consumerId}")
    public ResponseEntity<List<OrderDto>> getByConsumerId(@PathVariable Long consumerId) {
        List<OrderDto> orders = orderService.findByConsumerId(consumerId).stream()
                .map(orderMapper::toDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Update order price",
            description = "Update the price of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order price updated"),
        @ApiResponse(responseCode = "400", description = "Invalid price format", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "id", description = "Order ID", required = true)
    @Parameter(name = "price", description = "New price for the order", required = true)
    @PatchMapping("/{id}/price")
    public ResponseEntity<OrderDto> updatePrice(
            @PathVariable Long id,
            @RequestParam @Positive BigDecimal price) {
        OrderDto updatedOrder = orderMapper.toDto(orderService.updatePrice(id, price));
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Deactivate an order",
            description = "Soft delete (deactivate) an order by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "id", description = "Order ID", required = true)
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateOrderById(@PathVariable Long id) {
        orderService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete an order permanently",
            description = "Hard delete an order by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json"))
    })
    @Parameter(name = "id", description = "Order ID", required = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
