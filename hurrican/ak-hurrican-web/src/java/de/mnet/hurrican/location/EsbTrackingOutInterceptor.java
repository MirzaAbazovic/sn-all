package de.mnet.hurrican.location;

import java.net.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Copies the ESB specific headers fields which are needed for request tracking and adds some application properties
 * which are displayed in the ESB admin console.
 */
public class EsbTrackingOutInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private static final Set<String> TO_COPY = ImmutableSet.of("ESB_TrackingId", "ESB_Service", "ESB_Operation");
    private static final String ESB_COMPONENT_ID = "ESB_Comp";
    private static final String ESB_PROCESS_ID = "ESB_Process";

    private String component = "";
    private String processId;

    public EsbTrackingOutInterceptor() {
        super(Phase.SEND);
        String username = System.getProperty("user.name");
        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            // ignore
        }
        processId = username + "@" + hostname;
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        Exchange exchange = soapMessage.getExchange();
        Message inMessage = exchange.getInMessage();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null) {
            outMessage = exchange.getOutFaultMessage();
        }
        if (outMessage == null) {
            return;
        }
        Map<String, Object> outHeaders = (Map<String, Object>) outMessage.get(Message.PROTOCOL_HEADERS);
        if (outHeaders == null) {
            outHeaders = new TreeMap<>();
            outMessage.put(Message.PROTOCOL_HEADERS, outHeaders);
        }

        copyHeader(inMessage, outHeaders);
        outHeaders.put(ESB_COMPONENT_ID, ImmutableList.of(component));
        outHeaders.put(ESB_PROCESS_ID, ImmutableList.of(processId));
    }

    @SuppressWarnings("unchecked")
    private void copyHeader(Message inMessage, Map<String, Object> outHeaders) {
        if (inMessage == null) {
            return;
        }
        Map<String, Object> inHeaders = (Map<String, Object>) inMessage.get(Message.PROTOCOL_HEADERS);
        if (inHeaders == null) {
            return;
        }
        for (String key : TO_COPY) {
            Object value = inHeaders.get(key);
            if (value instanceof List) {
                outHeaders.put(key, value);
            }
            else if (value instanceof String) {
                outHeaders.put(key, ImmutableList.of(value));
            }
        }
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }
}
