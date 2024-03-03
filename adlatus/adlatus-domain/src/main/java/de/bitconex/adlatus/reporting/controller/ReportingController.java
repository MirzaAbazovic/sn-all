package de.bitconex.adlatus.reporting.controller;

import de.bitconex.adlatus.common.dto.Filters;
import de.bitconex.adlatus.reporting.dto.OrderHistoryDTO;
import de.bitconex.adlatus.reporting.dto.ReportingResponseDTO;
import de.bitconex.adlatus.reporting.service.reporting.ReportingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reporting")
public class ReportingController {
    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/reports/active")
    public ResponseEntity<ReportingResponseDTO> getResourceOrders(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  Filters filters) {
        ReportingResponseDTO resourceOrders = reportingService.getActiveResourceOrders(filters, page, size);
        return ResponseEntity.ok(resourceOrders);
    }

    @GetMapping("/reports/archived")
    public ResponseEntity<ReportingResponseDTO> getArchivedResourceOrders(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size, Filters filters) {
        ReportingResponseDTO resourceOrders = reportingService.getArchivedResourceOrders(filters, page, size);
        return ResponseEntity.ok(resourceOrders);
    }

    @GetMapping("/reports/history/{id}")
    public ResponseEntity<OrderHistoryDTO> getHistoryResourceOrders(@PathVariable String id) {
        OrderHistoryDTO orderHistoryDTO = reportingService.getResourceOrderHistory(id);
        return ResponseEntity.ok(orderHistoryDTO);
    }
}
