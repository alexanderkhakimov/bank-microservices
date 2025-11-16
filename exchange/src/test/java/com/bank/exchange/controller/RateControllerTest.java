package com.bank.exchange.controller;

import com.bank.exchange.dto.RateResponseDto;
import com.bank.exchange.dto.RateUiResponseDto;
import com.bank.exchange.dto.UpdateRateRequestDto;
import com.bank.exchange.service.RateService;
import com.bank.kafka.enums.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RateController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration.class
        })
@WithMockUser
public class RateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RateService rateService;


    @Test
    void shouldCreateRatesSuccessfully() throws Exception {
        final var rates = List.of(
                UpdateRateRequestDto.builder()
                        .currency(Currency.RUB)
                        .value(BigDecimal.ONE)
                        .build(),
                UpdateRateRequestDto.builder()
                        .currency(Currency.USD)
                        .value(BigDecimal.valueOf(1.25))
                        .build(),
                UpdateRateRequestDto.builder()
                        .currency(Currency.EUR)
                        .value(BigDecimal.valueOf(0.85))
                        .build()
        );

        mockMvc.perform(post("/api/exchange/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rates))
        ).andExpect(status().isCreated()
        ).andReturn();

        verify(rateService, times(1)).updateAll(anyList());
    }

    @Test
    void shouldOkRateUiSuccessfully() throws Exception {
        final var expected = List.of(
                RateUiResponseDto.builder()
                        .title(Currency.RUB.getTitle())
                        .name(Currency.RUB.name())
                        .value(BigDecimal.ONE)
                        .build(),
                RateUiResponseDto.builder()
                        .title(Currency.USD.getTitle())
                        .name(Currency.USD.name())
                        .value(BigDecimal.valueOf(1.25))
                        .build(),
                RateUiResponseDto.builder()
                        .title(Currency.EUR.getTitle())
                        .name(Currency.EUR.name())
                        .value(BigDecimal.valueOf(1.35))
                        .build()
        );
        when(rateService.getUiRatesAll()).thenReturn(expected);
        mockMvc.perform(get("/api/exchange/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.length()").value(3)
        ).andExpect(jsonPath("$[1].name").value("USD")
        ).andExpect(jsonPath("$[1].value").value(1.25));

        verify(rateService, times(1)).getUiRatesAll();
    }

    @Test
    void shouldOkRateSuccessfully() throws Exception {
        final var expected = List.of(
                RateResponseDto.builder()
                        .currency(Currency.RUB)
                        .value(BigDecimal.ONE)
                        .build(),
                RateResponseDto.builder()
                        .currency(Currency.USD)
                        .value(BigDecimal.valueOf(1.25))
                        .build(),
                RateResponseDto.builder()
                        .currency(Currency.EUR)
                        .value(BigDecimal.valueOf(0.85))
                        .build()
        );
        when(rateService.getRatesAll()).thenReturn(expected);
        mockMvc.perform(get("/api/exchange/rates")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.length()").value(3)
        ).andExpect(jsonPath("$[1].currency").value("USD")
        ).andExpect(jsonPath("$[1].value").value(1.25));

        verify(rateService, times(1)).getRatesAll();
    }
}
