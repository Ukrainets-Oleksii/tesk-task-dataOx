package tech.task.dataox.service.test.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.Order;
import tech.task.dataox.service.ClientService;
import tech.task.dataox.service.OrderService;
import tech.task.dataox.service.test.TestService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final ClientService clientService;
    private final OrderService orderService;

    Client supplier;
    Client consumer;

    @PostConstruct
    public void init() {
        supplier = createSupplier();
        consumer = createConsumer();
    }

    @Override
    public void caseOne() {
        Order o = Order.builder()
                .title("Book")
                .supplier(supplier)
                .consumer(consumer)
                .price(BigDecimal.TEN)
                .build();

        for (int i = 0; i < 5; i++) {
            orderService.create(o);
        }
    }

    @Override
    public void caseTwo() {
        Client consumerCaseTwo = Client.builder()
                .name("consumer")
                .lastName("TestCase2")
                .email("testCase2@example.com")
                .phone("1312436475")
                .profit(BigDecimal.valueOf(-970))
                .build();

        Client consumeCaseTwoSaved = clientService.createClient(consumerCaseTwo);

        for (int price = 10; price <= 100; price += 10) {
            Order order = Order.builder()
                    .title("Phone")
                    .supplier(supplier)
                    .consumer(consumeCaseTwoSaved)
                    .price(BigDecimal.valueOf(price))
                    .build();

            orderService.create(order);
        }
    }

    @Override
    public void caseThree() {
        int ordersToCreate = 5;

        // 1. Thread for deactivate client between creating orders
        new Thread(() -> {
            try {
                Thread.sleep(12000);
                clientService.deactivateById(consumer.getId());
                System.out.println("Client " + consumer.getId() + " deactivated");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 2. Create orders
        for (int i = 0; i < ordersToCreate; i++) {
            Order order = Order.builder()
                    .title("Laptop" + i)
                    .supplier(supplier)
                    .consumer(consumer)
                    .price(BigDecimal.valueOf(100 + i))
                    .build();

            orderService.create(order);
            System.out.println("Created order " + i);
        }
    }

    private Client createSupplier() {
        Client s = Client.builder()
                .email("test@test.com")
                .name("Supplier")
                .lastName("Test")
                .phone("123456789")
                .build();

        return clientService.createClient(s);
    }

    private Client createConsumer() {
        Client c = Client.builder()
                .email("testtest@test.com")
                .name("Consumer")
                .lastName("Test")
                .phone("987654321")
                .build();

        return clientService.createClient(c);
    }
}
