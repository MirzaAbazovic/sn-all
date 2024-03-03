package de.bitconex.adlatus.reporting.service.reporting;

import de.bitconex.adlatus.common.dto.Filters;
import de.bitconex.adlatus.reporting.dto.OrderHistoryDTO;
import de.bitconex.adlatus.reporting.dto.ReportingResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface ReportingService {

    ReportingResponseDTO getActiveResourceOrders(Filters filters, int page, int size);

    ReportingResponseDTO getArchivedResourceOrders(Filters filters, int page, int size);

    OrderHistoryDTO getResourceOrderHistory(String id);
}
