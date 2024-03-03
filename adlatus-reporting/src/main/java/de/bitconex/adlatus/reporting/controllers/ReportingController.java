package de.bitconex.adlatus.reporting.controllers;

import de.bitconex.adlatus.reporting.models.Filters;
import de.bitconex.adlatus.reporting.models.OrderHistory;
import de.bitconex.adlatus.reporting.models.ReportingResponse;
import de.bitconex.adlatus.reporting.services.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Controller
public class ReportingController {
    private final ReportingService reportingService;

    ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/reports/active")
    @ResponseBody
    public ReportingResponse getReports(@RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Filters filters) {
        return reportingService.getActiveResourceOrders(filters, sort, page, size);
    }

    @GetMapping("/reports/archived")
    @ResponseBody
    public ReportingResponse getArchivedReports(@RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                Filters filters) {
        return reportingService.getArchivedResourceOrders(filters, sort, page, size);
    }

    @GetMapping("/reports/history/{id}")
    @ResponseBody
    public OrderHistory getHistory(@PathVariable String id) {
        return reportingService.getOrderHistory(id);
    }
}
