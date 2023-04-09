package com.cryptocurrency.investment.price.util;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpJsonRequest {

    public <T> T sendRequest(String url, Class<T> clazz) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        try (InputStream responseBody = connection.getInputStream()) {
            return new ObjectMapper().readValue(responseBody, clazz);
        }
    }
}
