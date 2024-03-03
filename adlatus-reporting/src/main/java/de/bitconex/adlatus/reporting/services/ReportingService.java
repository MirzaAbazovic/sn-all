package de.bitconex.adlatus.reporting.services;

import de.bitconex.adlatus.reporting.models.Filters;
import de.bitconex.adlatus.reporting.models.OrderHistory;
import de.bitconex.adlatus.reporting.models.ReportingResponse;

public interface ReportingService {

    ReportingResponse getActiveResourceOrders(Filters filters, String sort, int page, int size);

    ReportingResponse getArchivedResourceOrders(Filters filters, String sort, int page, int size);

    OrderHistory getOrderHistory(String id);
}
