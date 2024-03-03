package de.bitconex.adlatus.reporting.services;

import de.bitconex.adlatus.reporting.models.Filters;
import de.bitconex.adlatus.reporting.models.OrderHistory;
import de.bitconex.adlatus.reporting.models.ReportingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportingServiceImpl implements ReportingService {

    @Value("${reporting.client.base-url}")
    private String baseUrl;

    @Autowired
    private ReportingClientService reportingClientService;

    public ReportingResponse getActiveResourceOrders(Filters filters, String sort, int page, int size) {

        return reportingClientService.fetchReports(filters, sort, page, size, baseUrl + "/reports/active");
    }

    public ReportingResponse getArchivedResourceOrders(Filters filters, String sort, int page, int size) {

        return reportingClientService.fetchReports(filters, sort, page, size, baseUrl + "/reports/archived");
    }

    @Override
    public OrderHistory getOrderHistory(String id) {
        return reportingClientService.fetchSingleReport(id, baseUrl + String.format("/reports/history/%s", id));
    }
}
