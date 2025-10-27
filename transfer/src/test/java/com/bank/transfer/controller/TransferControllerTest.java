package com.bank.transfer.controller;

import com.bank.transfer.TransferService.TransferService;
import com.bank.transfer.dto.TransferRequest;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransferController.class)
@WithMockUser
class TransferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferService transferService;


    @Test
    void processTransferOperation_ValidRequest_ReturnsOk() throws Exception {
        final var login = "userTest";
        final var request = new TransferRequest("USD", "EUR", new BigDecimal("100.0"), "userTest");

        mockMvc.perform(post("/{login}/transfer", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void processTransferOperation_InvalidRequest_ReturnsBadRequest() throws Exception {
        String login = "user123";
        TransferRequest invalidRequest = new TransferRequest("", "", new BigDecimal("-100"), "");

        mockMvc.perform(post("/{login}/transfer", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processTransferOperation_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        String login = "user123";
        TransferRequest request = new TransferRequest("USD", "EUR", new BigDecimal("100.00"), "user123");

        doThrow(new RuntimeException("Service error"))
                .when(transferService).processTransferOperation(eq(login), any());

        mockMvc.perform(post("/{login}/transfer", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
