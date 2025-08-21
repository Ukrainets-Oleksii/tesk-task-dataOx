package tech.task.dataox.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.task.dataox.model.Client;

import java.math.BigDecimal;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    @Query("""
        select c from Client c
        where c.isActive = true
          and (
               lower(c.name)     like lower(concat('%', :q, '%'))
            or lower(c.lastName) like lower(concat('%', :q, '%'))
            or lower(c.email)    like lower(concat('%', :q, '%'))
            or lower(c.address)  like lower(concat('%', :q, '%'))
            or c.phone           like concat('%', :q, '%')
          )
        """)
    Page<Client> searchActive(@Param("q") String q,
                              Pageable pageable);

    @Query("select c from Client c where c.profit between :minProfit and :maxProfit and c.isActive = true")
    Page<Client> findClientsByProfitBetween(Pageable pageable,
                                            @Param("minProfit") BigDecimal minProfit,
                                            @Param("maxProfit") BigDecimal maxProfit);

    @Modifying
    @Transactional
    @Query("update Client c set c.profit = 0")
    void resetAllProfit();
}
