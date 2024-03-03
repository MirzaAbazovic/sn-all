package de.bitconex.hub.eventing;

import lombok.Data;

@Data
public class NotificationRequest<T> {
    private T event;
}
