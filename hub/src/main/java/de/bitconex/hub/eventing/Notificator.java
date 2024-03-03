package de.bitconex.hub.eventing;

public interface Notificator {

    NotificationResponse notifyListener(NotificationRequest<?> notificationRequest);
}
