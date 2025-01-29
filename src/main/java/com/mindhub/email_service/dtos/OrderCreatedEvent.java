package com.mindhub.email_service.dtos;
import java.util.List;

public record OrderCreatedEvent(
        Long id,
        String email,
        OrderStatus status,
        List<NewProductDTO> products
) {
}
