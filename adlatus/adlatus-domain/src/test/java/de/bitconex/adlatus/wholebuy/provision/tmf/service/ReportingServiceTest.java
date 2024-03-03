package de.bitconex.adlatus.wholebuy.provision.tmf.service;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.reporting.service.reporting.ReportingService;
import de.bitconex.adlatus.reporting.service.reporting.ReportingServiceImpl;
import de.bitconex.adlatus.common.dto.Filters;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.reporting.dto.OrderHistoryDTO;
import de.bitconex.adlatus.reporting.dto.OrderMessageInfoDTO;
import de.bitconex.adlatus.reporting.dto.ReportingDTO;
import de.bitconex.adlatus.reporting.dto.ReportingResponseDTO;
import de.bitconex.adlatus.reporting.service.reporting.mapper.ReportingMapper;
import de.bitconex.adlatus.common.util.xml.XmlExtractor;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.common.model.WitaProductOutbox;
import de.bitconex.adlatus.inbox.service.WitaInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.scheduling.WitaOutboxService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


class ReportingServiceTest {

    private ResourceOrderService resourceOrderService;

    private WitaInboxService witaInboxService;

    private ReportingMapper reportingMapper;

    private WitaOutboxService witaOutboxService;

    private ReportingService cut;

    @BeforeEach
    void setUp() {
        resourceOrderService = Mockito.mock(ResourceOrderService.class);
        witaOutboxService = Mockito.mock(WitaOutboxService.class);
        witaInboxService = Mockito.mock(WitaInboxService.class); // Add this line

        reportingMapper = new ReportingMapper(new XmlExtractor());
        cut = new ReportingServiceImpl(reportingMapper, resourceOrderService, witaInboxService, witaOutboxService);
    }

    // TODO: IMPLEMENT FILTERING TEST

    @Test
    void testGetActiveResourceOrders() {
        Filters filters = new Filters();
        int page = 0;
        int size = 5;

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime nowPlusOneDay = now.plusDays(1);

        ResourceOrder order1 = new ResourceOrder();
        order1.setId("Order1");
        order1.setName("Order 1");
        order1.setOrderDate(now);

        ResourceOrder order2 = new ResourceOrder();
        order2.setId("Order2");
        order2.setName("Order 2");
        order2.setOrderDate(nowPlusOneDay);

        List<ResourceOrder> resourceOrders = Arrays.asList(order1, order2);
        Page<ResourceOrder> resourceOrderPage = new PageImpl<>(resourceOrders);

        ReportingDTO reporting1 = new ReportingDTO();
        reporting1.setOrderNumber(order1.getId());
        reporting1.setOrderName(order1.getName());
        reporting1.setOrderDate(now);

        ReportingDTO reporting2 = new ReportingDTO();
        reporting2.setOrderNumber(order2.getId());
        reporting2.setOrderName(order2.getName());
        reporting2.setOrderDate(nowPlusOneDay);

        List<ReportingDTO> expectedReportings = Arrays.asList(reporting1, reporting2);

        when(resourceOrderService.findAll(any(Query.class), any(Pageable.class))).thenReturn(resourceOrderPage);

        // Act
        ReportingResponseDTO actualReportings = cut.getActiveResourceOrders(filters, page, size);

        // Assert
        assertThat(expectedReportings.size()).isEqualTo(actualReportings.getReportingList().size());
        assertThat(expectedReportings.getFirst().getOrderName()).isEqualTo(actualReportings.getReportingList().getFirst().getOrderName());
        assertThat(expectedReportings.get(0).getOrderNumber()).isEqualTo(actualReportings.getReportingList().get(0).getOrderNumber());
        assertThat(expectedReportings.get(0).getOrderDate()).isEqualTo(actualReportings.getReportingList().get(0).getOrderDate());

        assertThat(expectedReportings.get(1).getOrderName()).isEqualTo(actualReportings.getReportingList().get(1).getOrderName());
        assertThat(expectedReportings.get(1).getOrderNumber()).isEqualTo(actualReportings.getReportingList().get(1).getOrderNumber());
        assertThat(expectedReportings.get(1).getOrderDate()).isEqualTo(actualReportings.getReportingList().get(1).getOrderDate());
    }

    @Test
    void testGetArchivedResourceOrders() {
        Filters filters = new Filters();
        int page = 0;
        int size = 5;

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime nowPlusOneDay = now.plusDays(1);

        ResourceOrder order1 = new ResourceOrder();
        order1.setId("Order1");
        order1.setName("Order 1");
        order1.setOrderDate(now);

        ResourceOrder order2 = new ResourceOrder();
        order2.setId("Order2");
        order2.setName("Order 2");
        order2.setOrderDate(nowPlusOneDay);

        List<ResourceOrder> resourceOrders = Arrays.asList(order1, order2);
        Page<ResourceOrder> resourceOrderPage = new PageImpl<>(resourceOrders);

        ReportingDTO reporting1 = new ReportingDTO();
        reporting1.setOrderNumber(order1.getId());
        reporting1.setOrderName(order1.getName());
        reporting1.setOrderDate(now);
        reporting1.setMessageInterface("WITA");

        ReportingDTO reporting2 = new ReportingDTO();
        reporting2.setOrderNumber(order2.getId());
        reporting2.setOrderName(order2.getName());
        reporting2.setOrderDate(nowPlusOneDay);
        reporting2.setMessageInterface("WITA");

        List<ReportingDTO> expectedReportings = Arrays.asList(reporting1, reporting2);

        when(resourceOrderService.findAll(any(Query.class), any(Pageable.class))).thenReturn(resourceOrderPage);

        // Act
        ReportingResponseDTO actualReportings = cut.getArchivedResourceOrders(filters, page, size);

        // Assert
        assertThat(expectedReportings.size()).isEqualTo(actualReportings.getReportingList().size());
        assertThat(expectedReportings.getFirst().getOrderName()).isEqualTo(actualReportings.getReportingList().getFirst().getOrderName());
        assertThat(expectedReportings.getFirst().getOrderNumber()).isEqualTo(actualReportings.getReportingList().getFirst().getOrderNumber());
        assertThat(expectedReportings.get(0).getOrderDate()).isEqualTo(actualReportings.getReportingList().get(0).getOrderDate());
        assertThat(expectedReportings.get(0).getMessageInterface()).isEqualTo(actualReportings.getReportingList().get(0).getMessageInterface());

        assertThat(expectedReportings.get(1).getOrderName()).isEqualTo(actualReportings.getReportingList().get(1).getOrderName());
        assertThat(expectedReportings.get(1).getOrderNumber()).isEqualTo(actualReportings.getReportingList().get(1).getOrderNumber());
        assertThat(expectedReportings.get(1).getOrderDate()).isEqualTo(actualReportings.getReportingList().get(1).getOrderDate());
        assertThat(expectedReportings.get(1).getMessageInterface()).isEqualTo(actualReportings.getReportingList().get(1).getMessageInterface());
    }

    @Test
    @SneakyThrows
    public void testGetHistoryResourceOrders() {
        // Arrange
        String id = "Order1";
        ResourceOrder resourceOrder = new ResourceOrder();
        resourceOrder.setId(id);

        String message1 = FileTestUtil.readResourceContent("payloads/wita/v15/qeb/QEB_test_message.xml");
        WitaProductInbox witaProductInbox1 = new WitaProductInbox();
        witaProductInbox1.setMessage(message1);
        witaProductInbox1.setExternalOrderId(id);
        witaProductInbox1.setStatus(WitaProductInbox.Status.PROCESSED);

        String message2 = FileTestUtil.readResourceContent("payloads/wita/v15/abm/order_12345_message.xml");
        WitaProductInbox witaProductInbox2 = new WitaProductInbox();
        witaProductInbox2.setMessage(message2);
        witaProductInbox2.setExternalOrderId(id);
        witaProductInbox2.setStatus(WitaProductInbox.Status.PROCESSED);

        List<WitaProductInbox> witaInbox = Arrays.asList(witaProductInbox1, witaProductInbox2);

        String message = FileTestUtil.readResourceContent("payloads/wita/v15/createOrderMessage/createOrderMessage.xml");
        WitaProductOutbox witaProductOutbox1 = new WitaProductOutbox();
        witaProductOutbox1.setExternalOrderId(id);
        witaProductOutbox1.setMessage(message);

        List<WitaProductOutbox> witaOutbox = Arrays.asList(witaProductOutbox1);

        OrderMessageInfoDTO orderMessageInfo1 = new OrderMessageInfoDTO();
        orderMessageInfo1.setMessageType("MeldungstypQEBType");
        orderMessageInfo1.setArrivalTime("2023-11-23T19:21:58.528+01:00");
        orderMessageInfo1.setMessageInterface("WITA15");

        OrderMessageInfoDTO orderMessageInfo2 = new OrderMessageInfoDTO();
        orderMessageInfo2.setMessageType("MeldungstypABMType");
        orderMessageInfo2.setArrivalTime("2023-11-20T19:48:06.959+01:00");
        orderMessageInfo2.setMessageInterface("WITA15");

        OrderMessageInfoDTO orderMessageInfo3 = new OrderMessageInfoDTO();
        orderMessageInfo3.setMessageType("NEU Bereitstellung");
        orderMessageInfo3.setArrivalTime("2023-05-12T09:35:01.73+02:00");
        orderMessageInfo3.setMessageInterface("WITA15");

        List<OrderMessageInfoDTO> orderMessageInfoList = Arrays.asList(orderMessageInfo1, orderMessageInfo2, orderMessageInfo3);

        when(resourceOrderService.findById(id)).thenReturn(resourceOrder);
        when(witaInboxService.findByExternalOrderId(id)).thenReturn(witaInbox);
        when(witaOutboxService.findByExternalOrderId(id)).thenReturn(witaOutbox);

        // Act
        OrderHistoryDTO orderHistoryDTO = cut.getResourceOrderHistory(id);

        // Assert
        assertEquals(orderMessageInfoList.size(), orderHistoryDTO.getOrderMessageInfo().size());
        assertEquals(orderMessageInfoList.get(0).getMessageType(), orderHistoryDTO.getOrderMessageInfo().get(0).getMessageType());
        assertEquals(orderMessageInfoList.get(0).getArrivalTime(), orderHistoryDTO.getOrderMessageInfo().get(0).getArrivalTime());
        assertEquals(orderMessageInfoList.get(1).getMessageType(), orderHistoryDTO.getOrderMessageInfo().get(1).getMessageType());
        assertEquals(orderMessageInfoList.get(1).getArrivalTime(), orderHistoryDTO.getOrderMessageInfo().get(1).getArrivalTime());
        assertEquals(orderMessageInfo3.getMessageType(), orderHistoryDTO.getOrderMessageInfo().get(2).getMessageType());
        assertEquals(orderMessageInfo3.getArrivalTime(), orderHistoryDTO.getOrderMessageInfo().get(2).getArrivalTime());
        assertEquals(orderMessageInfo3.getMessageInterface(), orderHistoryDTO.getOrderMessageInfo().get(2).getMessageInterface());
    }
}