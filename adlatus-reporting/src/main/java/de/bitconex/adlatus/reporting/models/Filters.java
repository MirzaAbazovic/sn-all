package de.bitconex.adlatus.reporting.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filters {
    private LocalDate fromOrderDate;
    private LocalDate toOrderDate;
    private String orderId;
    private String lineId;
}
