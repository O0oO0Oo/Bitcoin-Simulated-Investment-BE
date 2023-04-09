package com.cryptocurrency.investment.transaction.controller;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.transaction.service.ReservedTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(
        controllers = ReservedTransactionController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class ReservedTransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private ReservedTransactionService reservedTransactionService;

    @Test
    @DisplayName("에약 거래 목록 조회 GET")
    void reservedList_whenGetValidRequest_thenReturn1000Code() throws Exception {
        // given
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/cryptos/tx/reserved")
                        .accept(MediaType.APPLICATION_JSON);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
                .andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto = new ObjectMapper().readValue(
                responseBodyAsString, ResponseWrapperDto.class
        );

        // then
        Assertions.assertEquals(1000,responseWrapperDto.code());
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    @DisplayName("예약 거래 등록 POST")
    void reservedAdd_whenGetValidRequest_thenReturn1000Code() throws Exception {
        // given
        String request = "" +
                "{" +
                "\"name\":\"BTC\"," +
                "\"price\":\"1000\"," +
                "\"amount\":\"10\"," +
                "\"type\":\"RESERVE_BUY\"" +
                "}";
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/cryptos/tx/reserved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
                .andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto = new ObjectMapper().readValue(
                responseBodyAsString, ResponseWrapperDto.class
        );

        // then
        Assertions.assertEquals(1000,responseWrapperDto.code());
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    @DisplayName("예약 거래 삭제 DELETE")
    void reservedRemove_whenGetValidRequest_thenReturn1000Code() throws Exception {
        // given
        String request = "" +
                "{" +
                "\"name\":\"BTC\"," +
                "\"price\":\"1000\"," +
                "\"amount\":\"10\"," +
                "\"type\":\"RESERVE_BUY\"" +
                "}";
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/cryptos/tx/reserved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request);

        // when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
                .andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ResponseWrapperDto responseWrapperDto = new ObjectMapper().readValue(
                responseBodyAsString, ResponseWrapperDto.class
        );

        // then
        Assertions.assertEquals(1000,responseWrapperDto.code());
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }
}