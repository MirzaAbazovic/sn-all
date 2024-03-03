package de.bitconex.adlatus.reporting.service.reporting.mapper;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceCharacteristic;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem;
import de.bitconex.adlatus.reporting.dto.enums.MessageDirection;
import de.bitconex.adlatus.reporting.dto.OrderMessageInfoDTO;
import de.bitconex.adlatus.reporting.dto.ReportingDTO;
import de.bitconex.adlatus.common.util.xml.XmlExtractor;
import de.bitconex.adlatus.wholebuy.provision.dto.enums.XmlExtractorEnum;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.common.model.WitaProductOutbox;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingMapper {
    private final XmlExtractor xmlExtractor;

    public ReportingMapper(XmlExtractor xmlExtractor) {
        this.xmlExtractor = xmlExtractor;
    }

    public ReportingDTO mapToReporting(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder resourceOrder) {
        ReportingDTO reporting = new ReportingDTO();

        reporting.setOrderName(resourceOrder.getName());
        reporting.setOrderNumber(resourceOrder.getId());
        reporting.setOrderDate(resourceOrder.getOrderDate());
        reporting.setExpectedCompletionDate(resourceOrder.getExpectedCompletionDate());
        String lineId = null;
        if (resourceOrder != null) {
            List<ResourceOrderItem> resourceOrderItems = resourceOrder.getResourceOrderItems();
            if (resourceOrderItems != null && !resourceOrderItems.isEmpty()) {
                ResourceOrderItem firstItem = resourceOrderItems.get(0);
                if (firstItem != null) {
                    Resource resource = firstItem.getResource();
                    if (resource != null) {
                        List<ResourceCharacteristic> resourceCharacteristics = resource.getResourceCharacteristic();
                        if (resourceCharacteristics != null) {
                            lineId = resourceCharacteristics.stream()
                                .filter(resourceCharacteristic -> resourceCharacteristic.getName().equals("lineId"))
                                .map(ResourceCharacteristic::getValue)
                                .findFirst()
                                .orElse(null);
                        }
                    }
                }
            }
        }
        reporting.setLineID(lineId);
        //TODO: Change WITA to corresponding interface
        reporting.setMessageInterface("WITA");
        if (resourceOrder.getState() != null)
            reporting.setStatus(resourceOrder.getState().toString());

        return reporting;
    }

    public OrderMessageInfoDTO mapToOrderMessageInfo(WitaProductInbox witaProductInbox) {
        OrderMessageInfoDTO orderMessageInfo = new OrderMessageInfoDTO();

        orderMessageInfo.setMessageType(xmlExtractor.getMeldungstypType(witaProductInbox.getMessage()));
        orderMessageInfo.setArrivalTime(xmlExtractor.getZeitstempel(witaProductInbox.getMessage()));
        orderMessageInfo.setMessageInterface("WITA" + xmlExtractor.getInterfaceVersion(witaProductInbox.getMessage()));
        orderMessageInfo.setMessage(witaProductInbox.getMessage());
        orderMessageInfo.setMessageDirection(MessageDirection.RECEIVED);

        return orderMessageInfo;
    }

    public OrderMessageInfoDTO mapToOrderMessageInfo(WitaProductOutbox witaProductOutbox) {
        OrderMessageInfoDTO orderMessageInfo = new OrderMessageInfoDTO();

        orderMessageInfo.setMessageType(XmlExtractorEnum.ORDER_TYPE.getExpression());
        orderMessageInfo.setArrivalTime(xmlExtractor.getZeitstempel(witaProductOutbox.getMessage()));
        orderMessageInfo.setMessageInterface("WITA" + xmlExtractor.getInterfaceVersion(witaProductOutbox.getMessage()));
        orderMessageInfo.setMessage(witaProductOutbox.getMessage());
        orderMessageInfo.setMessageDirection(MessageDirection.SENT);

        return orderMessageInfo;
    }
}
