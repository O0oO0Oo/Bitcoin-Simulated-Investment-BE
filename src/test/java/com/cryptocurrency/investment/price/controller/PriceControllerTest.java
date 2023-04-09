package com.cryptocurrency.investment.price.controller;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.price.Service.PriceInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

@WebMvcTest(
        controllers = PriceController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class PriceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PriceController priceController;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private PriceInfoService priceInfoService;

    @Test
    @DisplayName("초당 가격 데이터, 유요한 요청 - 성공")
    void redisPriceList_whenGetValidRequest_thenReturnSucceedCode() throws Exception {
        // given
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/cryptos/price/btc/1/s")
                .accept(MediaType.APPLICATION_JSON);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto =
                new ObjectMapper().readValue(contentAsString, ResponseWrapperDto.class);

        // then
        Assertions.assertThat(responseWrapperDto.code()).isEqualTo(1000);
    }

    @Test
    @DisplayName("초당 가격 데이터, 유요한 요청 - 실패")
    void redisPriceList_whenValidRequest_thenReturnFailCode() throws Exception {
        // given
        BDDMockito.given(priceInfoService.redisFindPrice(Mockito.anyString(), Mockito.anyLong()))
                .willThrow(new RuntimeException("Internal Error"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/cryptos/price/btc/1/s")
                .accept(MediaType.APPLICATION_JSON);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto =
                new ObjectMapper().readValue(contentAsString, ResponseWrapperDto.class);

        // then
        Assertions.assertThat(responseWrapperDto.code()).isEqualTo(2100);
    }

    @Test
    @DisplayName("일/시/분 가격 데이터, 유요한 요청 - 성동")
    void mysqlPriceList_whenValidRequest_thenReturnSucceesCode() throws Exception {
        // given
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/cryptos/price/btc/1/m")
                .accept(MediaType.APPLICATION_JSON);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto =
                new ObjectMapper().readValue(contentAsString, ResponseWrapperDto.class);

        // then
        Assertions.assertThat(responseWrapperDto.code()).isEqualTo(1000);
    }

    @Test
    @DisplayName("일/시/분 가격 데이터, 유요한 요청 - 실패")
    void mysqlPriceList_whenValidRequest_thenReturnFailCode() throws Exception {
        // given
        BDDMockito.given(priceInfoService.mysqlFindPrice(Mockito.anyString(), Mockito.anyLong(), Mockito.anyString()))
                .willThrow(new RuntimeException("Internal Error"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/cryptos/price/btc/1/m")
                .accept(MediaType.APPLICATION_JSON);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto =
                new ObjectMapper().readValue(contentAsString, ResponseWrapperDto.class);

        // then
        Assertions.assertThat(responseWrapperDto.code()).isEqualTo(2100);
    }
}