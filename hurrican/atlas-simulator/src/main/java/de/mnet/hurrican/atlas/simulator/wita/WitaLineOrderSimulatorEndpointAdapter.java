package de.mnet.hurrican.atlas.simulator.wita;

import java.util.regex.*;
import javax.xml.namespace.*;
import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.message.Message;

import de.mnet.hurrican.simulator.handler.SimulatorEndpointAdapter;
import de.mnet.hurrican.atlas.simulator.wita.builder.AbstractWitaTest;

/**
 * Message handler evaluates WITA version from incoming request and invokes test builder accordingly. Handler can handle
 * different WITA versions simultaneously.
 *
 *
 */
public class WitaLineOrderSimulatorEndpointAdapter extends SimulatorEndpointAdapter {

    @Override
    public Message prepareRequestMessage(Message request) {
        QName rootQName = getRootQName(request);
        WitaLineOrderServiceVersion interfaceVersion = WitaLineOrderServiceVersion.fromNamespace(rootQName.getNamespaceURI());
        ErrorVersion errVersion = ErrorVersion.fromNamespace(rootQName.getNamespaceURI());

        if(errVersion != null) {
            String externalOrderId = extractExternalOrderId(request.getPayload().toString());

            request.setHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID, externalOrderId);
            return request;
        } else {
            String externalOrderId = extractExternalOrderId(request, interfaceVersion);
            String contractId = extractContractId(request, interfaceVersion);
            String customerId = extractCustomerId(request, interfaceVersion);
            String thirdPartySalesmanCustomerId = extractThirdPartySalesmanCustomerId(request, interfaceVersion);

            request.setHeader(WitaLineOrderMessageHeaders.INTERFACE_NAMESPACE, rootQName.getNamespaceURI())
                   .setHeader(WitaLineOrderMessageHeaders.INTERFACE_VERSION, interfaceVersion)
                   .setHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID, externalOrderId)
                   .setHeader(WitaLineOrderMessageHeaders.CONTRACT_ID, contractId)
                   .setHeader(WitaLineOrderMessageHeaders.CUSTOMER_ID, customerId)
                   .setHeader(WitaLineOrderMessageHeaders.THIRD_PARTY_SALESMAN_CUSTOMER_ID, thirdPartySalesmanCustomerId);
             return request;
        }
    }

    private String extractExternalOrderId(String request) {
        return extractElementText("externeAuftragsnummer", request);
    }

    private String extractElementText(String elementName, String payload) {
        String pattern = String.format(".*&lt;%s&gt;(\\S+)&lt;/%s&gt;.*",elementName, elementName);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(payload);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public String getOrderId(Message request) {
        if (request.getHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID) != null) {
            return request.getHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID).toString();
        }

        return null;
    }

    /**
     * Prepare test builder with WITA version.
     *
     * @param request
     * @param testBuilder
     */
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void prepareExecution(Message request, CitrusTestBuilder testBuilder) {
        if (testBuilder instanceof AbstractWitaTest) {
            if (request.getHeader(WitaLineOrderMessageHeaders.INTERFACE_VERSION) != null) {
                ((AbstractWitaTest) testBuilder).setServiceVersion((WitaLineOrderServiceVersion) request.getHeader(WitaLineOrderMessageHeaders.INTERFACE_VERSION));
            }

            ((AbstractWitaTest) testBuilder).setExternalOrderId(request.getHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID) != null ? request.getHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID).toString() : "");
        }
    }

    private String extractExternalOrderId(Message request, WitaLineOrderServiceVersion interfaceVersion) {
        return extractId(request, WitaLineOrderXpathExpressions.EXTERNAL_ORDER_ID.getXpath(interfaceVersion));
    }

    private String extractContractId(Message request, WitaLineOrderServiceVersion interfaceVersion) {
        return extractId(request, WitaLineOrderXpathExpressions.NOTIFICATION_CONTRACT_NUMMER.getXpath(interfaceVersion));
    }

    private String extractCustomerId(Message request, WitaLineOrderServiceVersion interfaceVersion) {
        return extractId(request, WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(interfaceVersion));
    }

    private String extractThirdPartySalesmanCustomerId(Message request, WitaLineOrderServiceVersion interfaceVersion) {
        return extractId(request, WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(interfaceVersion));
    }

}
