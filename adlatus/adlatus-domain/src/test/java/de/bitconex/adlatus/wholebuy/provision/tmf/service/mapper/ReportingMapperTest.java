package de.bitconex.adlatus.wholebuy.provision.tmf.service.mapper;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.reporting.service.reporting.mapper.ReportingMapper;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.reporting.dto.enums.MessageDirection;
import de.bitconex.adlatus.reporting.dto.OrderMessageInfoDTO;
import de.bitconex.adlatus.reporting.dto.ReportingDTO;
import de.bitconex.adlatus.common.util.xml.XmlExtractor;
import de.bitconex.adlatus.wholebuy.provision.dto.enums.XmlExtractorEnum;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.common.model.WitaProductOutbox;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ReportingMapperTest {


    private ReportingMapper cut;

    private XmlExtractor xmlExtractor;

    @BeforeEach
    void setUp() {
        xmlExtractor = new XmlExtractor();
        cut = new ReportingMapper(xmlExtractor);
    }

    @Test
    void testMapToReporting() {
        // Arrange

        var timeOffset = OffsetDateTime.now();

        ResourceOrder resourceOrder = new ResourceOrder();
        resourceOrder.setId("Order1254345");
        resourceOrder.setName("Order 152345");
        resourceOrder.setOrderDate(timeOffset);
        resourceOrder.setStartDate(timeOffset.plusDays(1));
        resourceOrder.setExpectedCompletionDate(timeOffset.plusDays(2));
        resourceOrder.setState(ResourceOrder.ResourceOrderState.ACKNOWLEDGED);

        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("Customer1453");
        relatedParty.setName("Customer Name");
        resourceOrder.setRelatedParties(Collections.singletonList(relatedParty));

        // Act
        ReportingDTO reporting = cut.mapToReporting(resourceOrder);

        // Assert
        assertEquals(resourceOrder.getId(), reporting.getOrderNumber());
        assertEquals(resourceOrder.getName(), reporting.getOrderName());
        assertEquals(resourceOrder.getOrderDate(), reporting.getOrderDate());
        assertEquals(resourceOrder.getExpectedCompletionDate(), reporting.getExpectedCompletionDate());
        assertEquals(resourceOrder.getState().toString(), reporting.getStatus());
    }

    @Test
    @SneakyThrows
    void testMapToMessageInfo() {
        // Arrange
        String message1 = FileTestUtil.readResourceContent("payloads/wita/v15/qeb/QEB_test_message.xml");
        WitaProductInbox witaProductInbox = new WitaProductInbox();
        witaProductInbox.setMessage(message1);

        // Act
        OrderMessageInfoDTO orderMessageInfo = cut.mapToOrderMessageInfo(witaProductInbox);

        // Assert
        assertEquals(xmlExtractor.getMeldungstypType(witaProductInbox.getMessage()), orderMessageInfo.getMessageType());
        assertEquals(xmlExtractor.getZeitstempel(witaProductInbox.getMessage()), orderMessageInfo.getArrivalTime());
        assertEquals("WITA" + xmlExtractor.getInterfaceVersion(witaProductInbox.getMessage()), orderMessageInfo.getMessageInterface());
        assertEquals(witaProductInbox.getMessage(), orderMessageInfo.getMessage());
    }

    @SneakyThrows
    @Test
    void testMapToOrderMessageInfo() {
        // Arrange
        WitaProductOutbox witaProductOutbox = new WitaProductOutbox();
        String message = FileTestUtil.readResourceContent("payloads/wita/v15/createOrderMessage/createOrderMessage.xml");
        witaProductOutbox.setMessage(message);

        // Act
        OrderMessageInfoDTO orderMessageInfo = cut.mapToOrderMessageInfo(witaProductOutbox);

        // Assert
        assertEquals(XmlExtractorEnum.ORDER_TYPE.getExpression(), orderMessageInfo.getMessageType());
        assertEquals(xmlExtractor.getZeitstempel(witaProductOutbox.getMessage()), orderMessageInfo.getArrivalTime());
        assertEquals("WITA" + xmlExtractor.getInterfaceVersion(witaProductOutbox.getMessage()), orderMessageInfo.getMessageInterface());
        assertEquals(witaProductOutbox.getMessage(), orderMessageInfo.getMessage());
        assertEquals(MessageDirection.SENT, orderMessageInfo.getMessageDirection());
    }

}