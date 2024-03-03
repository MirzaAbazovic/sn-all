package de.bitconex.adlatus.reporting.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OrderMessageInfo implements Serializable {
    String messageType;
    String arrivalTime;
    String message;
    String messageInterface;
    MessageDirection messageDirection;
}
