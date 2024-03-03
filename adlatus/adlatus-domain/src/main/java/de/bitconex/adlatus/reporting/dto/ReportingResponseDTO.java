package de.bitconex.adlatus.reporting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
public class ReportingResponseDTO implements Serializable {
    private long numberOfPages;
    private List<ReportingDTO> reportingList;

    public void addReporting(ReportingDTO reporting) {
        if (reportingList == null) {
            reportingList = new ArrayList<>();
        }
        reportingList.add(reporting);
    }
}

