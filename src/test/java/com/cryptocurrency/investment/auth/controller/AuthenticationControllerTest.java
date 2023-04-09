package com.cryptocurrency.investment.auth.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "spring.config.location=classpath:/application-test.yaml")
class AuthenticationControllerTest {
    @LocalServerPort
    private int localServerPort;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("Login Succeed")
    void testLoginUser_whenValidUserDetailsProvided_thenReturnJWT() throws JSONException {
        // given
        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        JSONObject loginRequestJson = new JSONObject();
        loginRequestJson.put("email", "test@frincoin.com");
        loginRequestJson.put("password", "test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(loginRequestJson.toString(), headers);
        // when
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/login", request, String.class);
        Map<String, Object> stringObjectMap = jacksonJsonParser.parseMap(responseEntity.getBody());
        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(1000, stringObjectMap.get("code"),() -> "failed");
    }
}