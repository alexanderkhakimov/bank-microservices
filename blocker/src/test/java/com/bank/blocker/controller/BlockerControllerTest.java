package com.bank.blocker.controller;

import com.bank.blocker.dto.BlockUserRequestDto;
import com.bank.blocker.enums.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.net.ssl.SSLEngineResult;

import java.util.random.RandomGenerator;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlockerController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration.class
        })
@WithMockUser
public class BlockerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private RandomGenerator randomGenerator;

    @ParameterizedTest
    @CsvSource({
            "true, 200",
            "false, 418"
    })
    void blockUser_WithDifferentRandomResults_ReturnsExpectedStatus(boolean randomResult, int expectedStatus) throws Exception {
        final var login = "testUser";
        final var request = BlockUserRequestDto.builder()
                .login(login)
                .operation("Операция снятие наличных денег")
                .currency(Currency.EUR)
                .build();

        when(randomGenerator.nextBoolean()).thenReturn(randomResult);

        mockMvc.perform(post("/api/blocker")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().is(expectedStatus));
    }

}
