package de.bitconex.adlatus.wholebuy.provision.it.util;


import com.github.tomakehurst.wiremock.client.MappingBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WitaMockingUtil {

    public static final String SOAP_ORDER_ACTION = "http://wholesale.telekom.de/oss/v15/wholesale/annehmenAuftrag";

    public static MappingBuilder stubOrderEndpoint() throws IOException {
        final String witaOrderResponse = FileTestUtil.readResourceContent("payloads/wita/v15/wita_order_response.xml");

        return post(urlEqualTo("/ws"))
                .withHeader("SOAPAction", containing(SOAP_ORDER_ACTION))
                .withHeader(HttpHeaders.ACCEPT, containing(MediaType.TEXT_XML_VALUE).or(containing(MediaType.APPLICATION_XML_VALUE)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(witaOrderResponse));
    }
}
