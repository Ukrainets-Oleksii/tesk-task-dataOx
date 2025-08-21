package tech.task.dataox.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.dto.UpdateClientDto;

import java.math.BigDecimal;

public interface ClientService {

    Client createClient(Client client);

    Client findClientById(Long id);

    Page<Client> findClientsByKeyword(String keyword, Pageable pageable);

    Client update(Long id, UpdateClientDto dto);

    BigDecimal findClientProfitById(Long id);

    Page<Client> findClientsByProfitBetween(Pageable pageable, BigDecimal min, BigDecimal max);

    void deactivateById(Long id);

    void recoverClientById(Long id);

    void resetAllProfit();
}