package de.bitconex.adlatus.common.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filters implements Serializable {
    private LocalDate fromOrderDate;
    private LocalDate toOrderDate;
    private String orderId;
    private String lineId;

    @Getter
    public enum Property {
        ORDER_ID("orderId"),
        STATE("state"),
        ORDER_DATE("orderDate"),
        LINE_ID("lineId", "resourceOrderItems.resource.resourceCharacteristic", "name", "value");

        private String propertyName;
        private String extractor;
        private String key;
        private String value;

        Property(String propertyName, String extractor, String key, String value) {
            this.propertyName = propertyName;
            this.extractor = extractor;
            this.key = key;
            this.value = value;
        }

        Property(String propertyName) {
            this.propertyName = propertyName;
        }
    }
}
