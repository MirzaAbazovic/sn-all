package de.bitconex.adlatus.reporting.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderHistory implements Serializable {
    private Reporting report;
    List<OrderMessageInfo> orderMessageInfo;
}