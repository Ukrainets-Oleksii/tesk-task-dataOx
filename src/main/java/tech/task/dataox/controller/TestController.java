package tech.task.dataox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.task.dataox.service.test.TestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Tag(name = "Test Scenarios", description = "Controller for testing different order creation scenarios")
public class TestController {

    private final TestService testService;

    // case 1
    @Operation(
            summary = "Case 1: N+1 identical orders",
            description = "The user mistakenly sends a request to create N+1 identical orders with price = 1. "
                    + "Only one valid order should be created, all the others must return an error."
    )
    @GetMapping("/caseOne")
    public ResponseEntity<Void> caseOne() {
        testService.caseOne();
        return ResponseEntity.ok().build();
    }

    // case 2
    @Operation(
            summary = "Case 2: 10 identical orders with decreasing price",
            description = "The user mistakenly sends a request to create 10 identical orders with price values decreasing "
                    + "from 100 to 10 (step 10). The buyer’s profit at the moment of request is -970. "
                    + "Only one valid order should be created."
    )
    @GetMapping("/caseTwo")
    public ResponseEntity<Void> caseTwo() {
        testService.caseTwo();
        return ResponseEntity.ok().build();
    }

    // case 3
    @Operation(
            summary = "Case 3: N+1 different orders and client deactivation",
            description = "The user sends a request to create N+1 different orders. "
                    + "At the same moment the client is deactivated (via API). "
                    + "Only the orders that were processed before the client became inactive should be created."
    )
    @GetMapping("/caseThree")
    public ResponseEntity<Void> caseThree() {
        testService.caseThree();
        return ResponseEntity.ok().build();
    }
}