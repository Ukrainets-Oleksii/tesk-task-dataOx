package tech.task.dataox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.task.dataox.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByTitleAndSupplierIdAndConsumerId(String title, Long supplierId, Long consumerId);

    List<Order> findBySupplierId(Long supplierId);

    List<Order> findByConsumerId(Long consumerId);

    @Query("SELECT o FROM Order o WHERE o.supplier.id = :userId OR o.consumer.id = :userId")
    List<Order> findAllByUserId(Long userId);
}
