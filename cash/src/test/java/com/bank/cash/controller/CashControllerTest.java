package com.bank.cash.controller;

import com.bank.cash.dto.CashRequest;
import com.bank.cash.enums.CashAction;
import com.bank.cash.enums.Currency;
import com.bank.cash.service.CashService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser
@WebMvcTest(
        controllers = CashController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration.class
        }
)
public class CashControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CashService cashService;

    @Test
    void processCashOperation_ValidRequest_ReturnsOk() throws Exception {

        String login = "testUser";
        CashRequest cashRequest = CashRequest.builder()
                .currency(Currency.USD.getTitle())
                .value(new BigDecimal("100.00"))
                .action(CashAction.DEPOSIT)
                .build();

        doNothing().when(cashService).processCashOperation(eq(login), any(CashRequest.class));

        mockMvc.perform(post("/user/{login}/cash", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cashRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void processCashOperation_InvalidRequest_ReturnsBadRequest() throws Exception {

        String login = "testUser";
        CashRequest cashRequest = CashRequest.builder()
                .currency("")
                .value(new BigDecimal("-100.00"))
                .action(null)
                .build();

        mockMvc.perform(post("/user/{login}/cash", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cashRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processTransferOperation_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        String login = "testUser";
        CashRequest cashRequest = CashRequest.builder()
                .currency(Currency.USD.getTitle())
                .value(new BigDecimal("100.00"))
                .action(CashAction.DEPOSIT)
                .build();

        doThrow(new RuntimeException("Service error"))
                .when(cashService).processCashOperation(eq(login), any());

        mockMvc.perform(post("/user/{login}/cash", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cashRequest)))
                .andExpect(status().isInternalServerError());
    }
}
