package de.bitconex.adlatus.reporting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderHistoryDTO implements Serializable {
    private ReportingDTO report;
    List<OrderMessageInfoDTO> orderMessageInfo;
}
