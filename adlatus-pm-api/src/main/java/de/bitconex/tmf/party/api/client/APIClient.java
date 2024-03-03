package de.bitconex.tmf.party.api.client;


import org.springframework.http.ResponseEntity;

public interface APIClient {
    <T> ResponseEntity<Void> post(String callbackUrl, T eventData);
}