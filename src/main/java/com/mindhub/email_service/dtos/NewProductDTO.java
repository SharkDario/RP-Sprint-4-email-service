package com.mindhub.email_service.dtos;

public record NewProductDTO(
        String name,
        String description,
        Double price,
        Integer stock) {
}
