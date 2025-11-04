package com.bank.transfer.TransferService;

import com.bank.transfer.config.properties.ClientProperties;
import com.bank.transfer.dto.RateResponseDto;
import com.bank.transfer.enums.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;
    @Mock
    private RestClient restClient;
    @Mock
    private ClientProperties clientProperties;
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ExchangeClient exchangeClient;

    @BeforeEach
    void setUp() {
        ClientProperties.ClientConfig exchangeConfig = new ClientProperties.ClientConfig();
        exchangeConfig.setBaseurl("http://test");
        when(clientProperties.getExchangeClient()).thenReturn(exchangeConfig);
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        exchangeClient = new ExchangeClient(restClientBuilder, clientProperties);
    }

    @Test
    void convert_ValidCurrencies_ReturnsConvertedAmount() {
        // Given
        List<RateResponseDto> rates = List.of(
                new RateResponseDto(Currency.USD, new BigDecimal("90.0")),
                new RateResponseDto(Currency.EUR, new BigDecimal("100.0"))
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/rates")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(rates);

        BigDecimal result = exchangeClient.convert(Currency.USD.name(), Currency.EUR.name(), new BigDecimal("100"));

        assertEquals(new BigDecimal("90.00"), result);
    }
}