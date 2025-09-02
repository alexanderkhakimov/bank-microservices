package com.bank.frontui.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdatePasswordRequest(
        @NotBlank String login,
        @NotBlank String password
) {
}
