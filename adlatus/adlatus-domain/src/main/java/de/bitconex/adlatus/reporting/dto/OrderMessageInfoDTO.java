package de.bitconex.adlatus.reporting.dto;

import de.bitconex.adlatus.reporting.dto.enums.MessageDirection;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OrderMessageInfoDTO implements Serializable {
    String messageType;
    String arrivalTime;
    String message;
    String messageInterface;
    MessageDirection messageDirection;
}
