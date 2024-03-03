package de.bitconex.tmf.party.api.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class APIClientImpl implements APIClient {

    private final RestTemplate restTemplate;

    public APIClientImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public <T> ResponseEntity<Void> post(String callbackUrl, T eventData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> requestEntity = new HttpEntity<>(eventData, headers);

        return restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, Void.class);
    }
}