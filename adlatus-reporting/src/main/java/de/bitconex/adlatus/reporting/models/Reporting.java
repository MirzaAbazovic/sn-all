package de.bitconex.adlatus.reporting.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
public class Reporting implements Serializable {
    private String orderName;
    private String orderNumber;
    private OffsetDateTime orderDate;
    private OffsetDateTime expectedCompletionDate;
    private String status;
    private String messageInterface;
    private String lineID;

    @Override
    public String toString() {
        return "Reporting{" +
                "orderName='" + orderName + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", startDate=" + orderDate +
                ", expectedDate=" + expectedCompletionDate +
                ", status='" + status + '\'' +
                ", messageInterface='" + messageInterface + '\'' +
                ", lineID='" + lineID + '\'' +
                '}';
    }
}
