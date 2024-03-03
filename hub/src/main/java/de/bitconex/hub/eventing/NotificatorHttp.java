package de.bitconex.hub.eventing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.bitconex.hub.model.EventSubscriptionOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Component
@Slf4j
public class NotificatorHttp implements Notificator {

    private static final String URI_FORMAT = "%s/listener/%s";

    private final EventRegistrationService eventRegistrationService;

    @Value("${hub.notificator.debug:false}")
    private Boolean debug;

    public NotificatorHttp(EventRegistrationService eventRegistrationService) {
        this.eventRegistrationService = eventRegistrationService;
    }

    @Override
    public NotificationResponse notifyListener(NotificationRequest<?> notificationRequest) {
        LOG.debug("notifyListener method called, request {}", notificationRequest);
        String eventName = notificationRequest.getEvent().getClass().getSimpleName();
        List<EventSubscriptionOutput> eventSubscriptions = eventRegistrationService.getByEvent(eventName);
        NotificationResponse notificationResponse = new NotificationResponse();
        eventSubscriptions.forEach(
                eventSubscription -> {
                    try {
                        String response = postToListener(notificationRequest, eventSubscription);
                        notificationResponse.addListenerResponse(eventSubscription.getId(), response);
                    } catch (Exception e) {
                        LOG.error("Error: {}", e);
                        notificationResponse.addListenerResponse(eventSubscription.getId(), e.getMessage());
                    }
                }
        );
        return notificationResponse;
    }

    private String postToListener(NotificationRequest<?> notificationRequest, EventSubscriptionOutput eventSubscription) throws URISyntaxException, IOException, InterruptedException {
        LOG.debug("postToListener method called, request {}", notificationRequest);
        String host = eventSubscription.getCallback();

        Object event = notificationRequest.getEvent();
        String eventName = event.getClass().getSimpleName();

        eventName = Character.toLowerCase(eventName.charAt(0)) + eventName.substring(1);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String eventJson = objectMapper.writeValueAsString(event);

        HttpRequest.BodyPublisher payload = HttpRequest.BodyPublishers.ofString(eventJson);
        URI uri1 = new URI(String.format(URI_FORMAT, host, eventName));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri1)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(payload)
                .build();

        if (this.debug) {
            LOG.debug("Notified listener {}, notification {}", eventSubscription, notificationRequest);
        }

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return String.valueOf(response.statusCode());
    }

}
