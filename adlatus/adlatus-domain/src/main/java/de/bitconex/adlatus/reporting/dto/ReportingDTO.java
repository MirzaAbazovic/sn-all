package de.bitconex.adlatus.reporting.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
public class ReportingDTO implements Serializable {
    private String orderName;
    private String orderNumber;
    private OffsetDateTime orderDate;
    private OffsetDateTime expectedCompletionDate;
    private String status;
    private String messageInterface;
    private String lineID;
}
