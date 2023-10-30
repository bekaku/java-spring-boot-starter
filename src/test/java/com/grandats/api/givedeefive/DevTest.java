package com.grandats.api.givedeefive;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class DevTest {

    @BeforeEach
    public void init() {
    }

    @Test
    public void should_generate_src_for_project() throws JSONException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2MzA5NTQxMS1mOTI1LTRjNGQtOTNmYS1mOTdiMjZjZDkwOTEiLCJpYXQiOjE2NTk0OTIzNzQsImV4cCI6MTY5MTAyODM3NH0.cDHlzDegHfIzIarav1BOAeVFd0VSKCrq2qBa-LEYQ-c");


//        JSONObject devOj = new JSONObject();
//        JSONObject devOjChild = new JSONObject();
//        devOjChild.put("id", 1);
//        devOj.put("dev", devOjChild);
//        HttpEntity<String> requestEntity = new HttpEntity<>(devOj.toString(), headers);
//        String personResultAsJsonStr = restTemplate.postForObject("http://localhost:8080/dev/development/generateSrc", requestEntity, String.class);
        //        Assertions.assertNotNull(personResultAsJsonStr);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<Object> response = restTemplate.exchange("http://localhost:8080/dev/development/generateSrcV2", HttpMethod.GET, request, Object.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    }
}
