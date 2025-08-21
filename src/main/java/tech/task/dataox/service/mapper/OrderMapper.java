package tech.task.dataox.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tech.task.dataox.model.Client;
import tech.task.dataox.model.Order;
import tech.task.dataox.model.dto.CreateOrderDto;
import tech.task.dataox.model.dto.OrderDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "mapClient")
    @Mapping(target = "consumer", source = "consumerId", qualifiedByName = "mapClient")
    @Mapping(target = "startProcessingAt", ignore = true)
    @Mapping(target = "endProcessingAt", ignore = true)
    @Mapping(target = "savedAt", ignore = true)
    Order toEntity(CreateOrderDto dto);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "consumerId", source = "consumer.id")
    OrderDto toDto(Order order);

    @Named("mapClient")
    default Client mapClient(Long id) {
        if (id == null) return null;
        Client client = new Client();
        client.setId(id);
        return client;
    }
}
