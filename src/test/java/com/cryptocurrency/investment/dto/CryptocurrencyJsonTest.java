package com.cryptocurrency.investment.dto;

import com.cryptocurrency.investment.domain.request.CryptocurrencyJson;
import com.cryptocurrency.investment.domain.request.CryptocurrencyJsonPriceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


class CryptocurrencyJsonTest {

    @Test
    void givenJsonData_whenthenPrintKeyPrice() throws IOException {
        //given
        URL url = new URL("https://api.bithumb.com/public/ticker/ALL");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type","application/json");

        InputStream responseBody = conn.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        CryptocurrencyJson readValue = mapper.readValue(responseBody, CryptocurrencyJson.class);
        HashMap<String, CryptocurrencyJsonPriceData> fields = readValue.getCryptocurrencyJsonInnerInfo().getFields();
        Long date = readValue.getCryptocurrencyJsonInnerInfo().getTimestamp();
        DateFormat df = new SimpleDateFormat("yy:MM:dd:HH:mm:ss");

        //when && then
        for (Map.Entry<String ,CryptocurrencyJsonPriceData> entry : fields.entrySet()
        ) {
            System.out.println(entry.getKey() + " = "  + entry.getValue().getClosing_price() + " Data : "
            + date + "  " + df.format(date));
        }
    }
}