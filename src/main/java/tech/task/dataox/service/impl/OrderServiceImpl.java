package tech.task.dataox.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tech.task.dataox.config.Constant;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.Order;
import tech.task.dataox.repository.ClientRepository;
import tech.task.dataox.repository.OrderRepository;
import tech.task.dataox.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public Order create(Order order) {
        log.debug("Attempting to create order...");
        // Basic null checks for required relations
        Long supplierId = (order.getSupplier() != null) ? order.getSupplier().getId() : null;
        Long consumerId = (order.getConsumer() != null) ? order.getConsumer().getId() : null;

        //extra check consumer and supplier
        if (supplierId == null || consumerId == null) {
            log.warn("Invalid order creation request: supplierId={} or consumerId={} is null", supplierId, consumerId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier or consumer are required");
        }
        //If the user creates an order on himself
        if (supplierId.equals(consumerId)) {
            log.warn("The supplier and the consumer cannot be the same");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier and consumer are the same");
        }
        //extra check price of order
        if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid order creation price was negative or zero: price={}", order.getPrice());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order price must be positive");
        }

        // Load managed Clients and validate active status
        Client supplier = clientRepository.findById(supplierId)
                .filter(Client::isActive)
                .orElseThrow(() -> {
                    log.warn("Supplier not found or inactive: id={}", supplierId);
                    return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Supplier not found or inactive");
                });
        Client consumer = clientRepository.findById(consumerId)
                .filter(Client::isActive)
                .orElseThrow(() -> {
                    log.warn("Consumer not found or inactive: id={}", consumerId);
                    return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Consumer not found or inactive");
                });

        // Profit threshold check for consumer: projected must be >= -1000
        log.info("Checking profit threshold for consumer id={}, currentProfit={}, orderPrice={}",
                consumerId, consumer.getProfit(), order.getPrice());
        BigDecimal projected = consumer.getProfit().subtract(order.getPrice());

        if (projected.compareTo(Constant.MIN_CONSUMER_PROFIT) <= 0) {//todo
            log.error("Consumer id={} profit would drop below allowed threshold: projected={}",
                    consumerId, projected);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Consumer profit would drop below -1000");
        }

        // Business key uniqueness: title + supplier + consumer
        if (orderRepository.existsByTitleAndSupplierIdAndConsumerId(order.getTitle(), supplierId, consumerId)) {
            log.error("Order already exists for title={}, supplierId={}, consumerId={}", order.getTitle(), supplierId, consumerId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists for given title/supplier/consumer");
        }

        log.info("Start processing order for supplierId={}, consumerId={}", supplierId, consumerId);
        order.setStartProcessingAt(LocalDateTime.now());

        // Simulate processing delay between 1 and 10 seconds
        int delaySec = ThreadLocalRandom.current().nextInt(1, 11);
        try {
            TimeUnit.SECONDS.sleep(delaySec);
            log.debug("Order processing simulated delay {} seconds completed", delaySec);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted while processing");
        }
        order.setEndProcessingAt(LocalDateTime.now());

        // Attach managed clients
        order.setSupplier(supplier);
        order.setConsumer(consumer);

        // Persist order
        Order saved = orderRepository.save(order);
        log.info("Finished processing order: id={}", saved.getId());

        // Update cached profits, with @Transactional we do not need save method from client repository
        supplier.setProfit(supplier.getProfit().add(order.getPrice()));
        consumer.setProfit(projected);
        log.info("Updated profits: supplierId={}, newProfit={}; consumerId={}, newProfit={}",
                supplierId, supplier.getProfit(), consumerId, consumer.getProfit());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        log.debug("Attempting to get order by id: id={}", id);
        return orderRepository.findById(id)
                .filter(Order::isActive)
                .orElseThrow((() -> {
                    log.warn("Order with id={} not found or deleted", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Order with id " + id + " not found.");
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllByClientId(Long clientId) {
        log.debug("Attempting to get all orders by userId id: id={}", clientId);
        return orderRepository.findAllByUserId(clientId).stream()
                .filter(Order::isActive)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findBySupplierId(Long supplierId) {
        log.debug("Attempting to get orders by supplier id: id={}", supplierId);
        return orderRepository.findBySupplierId(supplierId).stream()
                .filter(Order::isActive)
                .toList();
    }

    @Override
    public List<Order> findByConsumerId(Long consumerId) {
        log.debug("Attempting to get orders by consumer id: id={}", consumerId);
        return orderRepository.findByConsumerId(consumerId).stream()
                .filter(Order::isActive)
                .toList();
    }

    @Override
    @Transactional
    public Order updatePrice(Long id, BigDecimal newPrice) {
        log.debug("Attempting to update order id={} with new price={}", id, newPrice);

        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Price is below or equal to 0 : price={}", newPrice);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }

        Order order = findById(id);
        BigDecimal oldPrice = order.getPrice();
        order.setPrice(newPrice);

        Order updated = orderRepository.save(order);
        log.info("Updated order id={} with new price={}, old price={}", id, newPrice, oldPrice);
        return updated;
    }

    @Override
    @Transactional
    public void deactivateById(Long id) {
        log.debug("Attempting to deactivate order id={}", id);
        orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with id={} not found for soft-delete", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Order with id " + id + " not found");
                })
                .setActive(Boolean.FALSE);
        log.info("Order was soft-deleted: id={}", id);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Attempting to delete order id={}", id);
        if (!clientRepository.existsById(id)) {
            log.warn("Order with id={} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id " + id + " not found.");
        }
        orderRepository.deleteById(id);
        log.info("Order was deleted: id={}", id);
    }
}
