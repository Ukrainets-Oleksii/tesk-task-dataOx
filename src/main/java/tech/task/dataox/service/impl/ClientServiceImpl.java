package tech.task.dataox.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.dto.UpdateClientDto;
import tech.task.dataox.repository.ClientRepository;
import tech.task.dataox.service.ClientService;
import tech.task.dataox.service.mapper.ClientMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public Client createClient(Client client) {
        log.debug("Attempting to create client: email={}, name={} {}", client.getEmail(), client.getName(), client.getLastName());

        //Normalization of email and phone for standard format.
        String email = client.getEmail() != null ? client.getEmail().trim().toLowerCase() : null;
        String phone = client.getPhone() != null ? client.getPhone().trim() : null;
        //Set normalized email and phone to new client
        client.setEmail(email);
        client.setPhone(phone);

        //Check existing
        if (email != null && clientRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (phone != null && clientRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already in use");
        }

        Client saved = clientRepository.save(client);
        log.info("Client created: id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Client findClientById(Long id) {
        log.debug("Attempting to find client by id: id={}", id);
        return clientRepository.findById(id)
                .filter(Client::isActive)
                .orElseThrow((() -> {
                    log.warn("Client with id={} not found or deleted", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "client with id " + id + " not found.");
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> findClientsByKeyword(String keyword, Pageable pageable) {
        log.debug("Attempting to search clients: keyword='{}', page={}, size={}", keyword, pageable.getPageNumber(), pageable.getPageSize());
        return clientRepository.searchActive(keyword.trim(), pageable);
    }

    @Override
    @Transactional
    public Client update(Long id, UpdateClientDto dto) {
        log.debug("Attempting to update client: id={}", id);
        Client existing = findClientById(id);

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            if (clientRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                log.warn("Email conflict for id={}, email={} already in use", id, dto.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            existing.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null && !dto.getPhone().equals(existing.getPhone())) {
            if (clientRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
                log.warn("Phone conflict for id={}, phone={} already in use", id, dto.getPhone());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already in use");
            }
            existing.setPhone(dto.getPhone());
        }

        clientMapper.updateClientFromDto(dto, existing);
        log.info("Client updated id={} (email={}, phone={})",
                existing.getId(), existing.getEmail(), existing.getPhone());

        return existing;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal findClientProfitById(Long id) {
        return findClientById(id).getProfit();
    }

    @Override
    public Page<Client> findClientsByProfitBetween(Pageable pageable,
                                                   BigDecimal min, BigDecimal max) {
        return clientRepository.findClientsByProfitBetween(pageable, min, max);
    }

    @Override
    @Transactional
    public void deactivateById(Long id) {
        log.debug("Attempting to deactivate client: id={}", id);
        Client client = findClientById(id);
        client.setActive(Boolean.FALSE);
        client.setInactiveAt(LocalDateTime.now());
        log.info("Client deactivated id={} at {}", id, client.getInactiveAt());
    }

    @Override
    @Transactional
    public void recoverClientById(Long id) {
        log.debug("Attempting to recover client: id={}", id);
        Client client = clientRepository.findById(id)
                .filter(c -> !c.isActive())
                .orElseThrow((() -> {
                    log.warn("Client with id={} is active.", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "client with id " + id + " is active.");
                }));

        client.setActive(Boolean.TRUE);
        client.setInactiveAt(null);
        log.info("Client recovered id={}", id);
    }

    @Override
    public void resetAllProfit() {
        clientRepository.resetAllProfit();
    }
}
