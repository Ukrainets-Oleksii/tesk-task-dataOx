package tech.task.dataox.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.dto.ClientDto;
import tech.task.dataox.model.dto.CreateClientDto;
import tech.task.dataox.model.dto.UpdateClientDto;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client client);

    Client fromCreateDto(CreateClientDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClientFromDto(UpdateClientDto dto, @MappingTarget Client entity);
}