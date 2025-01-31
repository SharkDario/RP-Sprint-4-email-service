package com.mindhub.email_service.dtos;

public record WelcomeMessage(
        String username,
        String email,
        String token
) {
}