package de.bitconex.adlatus.reporting.service.reporting;

import de.bitconex.adlatus.common.dto.Filters;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.reporting.dto.OrderHistoryDTO;
import de.bitconex.adlatus.reporting.dto.OrderMessageInfoDTO;
import de.bitconex.adlatus.reporting.dto.ReportingDTO;
import de.bitconex.adlatus.reporting.dto.ReportingResponseDTO;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.reporting.service.reporting.mapper.ReportingMapper;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.inbox.service.WitaInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.scheduling.WitaOutboxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class ReportingServiceImpl implements ReportingService {
    // todo: Think about boundaries between order and reporting services

    private final ReportingMapper reportingMapper;
    private final ResourceOrderService resourceOrderService;
    private final WitaInboxService witaInboxService;
    private final WitaOutboxService witaOutboxService;

    public ReportingServiceImpl(ReportingMapper reportingMapper, ResourceOrderService resourceOrderService, WitaInboxService witaInboxService, WitaOutboxService witaOutboxService) {
        this.reportingMapper = reportingMapper;
        this.resourceOrderService = resourceOrderService;
        this.witaInboxService = witaInboxService;
        this.witaOutboxService = witaOutboxService;
    }

    private Page<ResourceOrder> getResourceOrders(int page, int size, Filters filters, boolean archived, BiFunction<Query, Pageable, Page<ResourceOrder>> fetchFunction) {
        Pageable pageable = PageRequest.of(page, size);

        Query query = generateQueryFromFilter(filters, archived);
        return fetchFunction.apply(query, pageable);
    }

    @Override
    public ReportingResponseDTO getActiveResourceOrders(Filters filters, int page, int size) {
        Page<ResourceOrder> resourceOrders = getResourceOrders(page, size, filters, false, resourceOrderService::findAll);
        if (resourceOrders == null) return ReportingResponseDTO.builder()
            .numberOfPages(1L)
            .reportingList(Collections.emptyList())
            .build();
        List<ReportingDTO> reportingList = resourceOrders.getContent().stream().map(reportingMapper::mapToReporting).toList();
        return ReportingResponseDTO.builder()
            .reportingList(reportingList)
            .numberOfPages(resourceOrders.getTotalPages())
            .build();
    }

    @Override
    public ReportingResponseDTO getArchivedResourceOrders(Filters filters, int page, int size) {
        Page<ResourceOrder> resourceOrders = getResourceOrders(page, size, filters, true, resourceOrderService::findAll);
        if (resourceOrders == null) return ReportingResponseDTO.builder()
            .numberOfPages(1L)
            .reportingList(Collections.emptyList())
            .build();
        List<ReportingDTO> reportingList = resourceOrders.getContent().stream().map(reportingMapper::mapToReporting).toList();
        return ReportingResponseDTO.builder()
            .reportingList(reportingList)
            .numberOfPages(resourceOrders.getTotalPages())
            .build();
    }

    @Override
    public OrderHistoryDTO getResourceOrderHistory(String id) {
        List<WitaProductInbox> witaInbox = witaInboxService.findByExternalOrderId(id);
        ResourceOrder resourceOrder = resourceOrderService.findById(id);
        var orderHistory = OrderHistoryDTO.builder()
            .report(reportingMapper.mapToReporting(resourceOrder));
        List<OrderMessageInfoDTO> receivedOrderMessages = witaInbox.stream().map(reportingMapper::mapToOrderMessageInfo).toList();
        List<OrderMessageInfoDTO> sentOrderMessages = witaOutboxService.findByExternalOrderId(id).stream().map(reportingMapper::mapToOrderMessageInfo).toList();

        List<OrderMessageInfoDTO> allMessages = new ArrayList<>();
        allMessages.addAll(receivedOrderMessages);
        allMessages.addAll(sentOrderMessages);

        orderHistory.orderMessageInfo(allMessages);
        return orderHistory.build();
    }

    public Query generateQueryFromFilter(Filters filters, boolean archived) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (archived) {
            criteriaList.add(Criteria.where(Filters.Property.STATE.getPropertyName()).in(ResourceOrder.ResourceOrderState.FAILED, ResourceOrder.ResourceOrderState.REJECTED, ResourceOrder.ResourceOrderState.CANCELLED, ResourceOrder.ResourceOrderState.COMPLETED));
        } else {
            criteriaList.add(Criteria.where(Filters.Property.STATE.getPropertyName()).nin(ResourceOrder.ResourceOrderState.FAILED, ResourceOrder.ResourceOrderState.REJECTED, ResourceOrder.ResourceOrderState.CANCELLED, ResourceOrder.ResourceOrderState.COMPLETED));
        }

        if (filters.getFromOrderDate() != null) {
            criteriaList.add(Criteria.where(Filters.Property.ORDER_DATE.getPropertyName()).gte(filters.getFromOrderDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        if (filters.getToOrderDate() != null) {
            criteriaList.add(Criteria.where(Filters.Property.ORDER_DATE.getPropertyName()).lt(filters.getToOrderDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        if (filters.getOrderId() != null) {
            criteriaList.add(Criteria.where(Filters.Property.ORDER_ID.getPropertyName()).regex(".*" + filters.getOrderId() + ".*"));
        }

        if (filters.getLineId() != null) {
            criteriaList.add(Criteria.where(Filters.Property.LINE_ID.getExtractor()).elemMatch(
                Criteria.where(Filters.Property.LINE_ID.getKey()).is(Filters.Property.LINE_ID.getPropertyName()).and(Filters.Property.LINE_ID.getValue()).regex(".*" + filters.getLineId() + ".*")
            ));
        }

        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        return query;
    }
}
