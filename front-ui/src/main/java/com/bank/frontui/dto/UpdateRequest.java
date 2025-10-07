package com.bank.frontui.dto;

import com.bank.frontui.model.AccountBalanceDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UpdateRequest(
        @NotNull @JsonProperty("name")  String name,
        @NotNull @JsonProperty("login") String login,
        @JsonProperty("account")  List<AccountBalanceDto> account,
        @NotNull @Past @JsonProperty("birthdate") LocalDate birthdate
) {
}
