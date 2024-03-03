package de.bitconex.adlatus.reporting.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
public class ReportingResponse implements Serializable {
    private long numberOfPages;
    private List<Reporting> reportingList;

    public void addReporting(Reporting reporting) {
        if (reportingList == null) {
            reportingList = new ArrayList<>();
        }
        reportingList.add(reporting);
    }
}
