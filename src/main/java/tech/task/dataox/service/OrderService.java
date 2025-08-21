package tech.task.dataox.service;

import tech.task.dataox.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Order create(Order order);

    Order findById(Long id);

    List<Order> findAllByClientId(Long userId);

    List<Order> findBySupplierId(Long supplierId);

    List<Order> findByConsumerId(Long consumerId);

    Order updatePrice(Long id, BigDecimal newPrice);

    void deactivateById(Long id);

    void deleteById(Long id);
}
