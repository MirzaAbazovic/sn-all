package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.inbox.service.WitaInboxService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WitaMessageEndpointIT extends IntegrationTestBase {

    public final static String WS_BASE_PATH = "/ws";
    public final static String SOAP_ACTION = "http://wholesale.telekom.de/oss/v15/wholesale/annehmenMeldung";

    @Autowired
    private WitaInboxService inboxService;

    @BeforeEach
    void setUp() {
        requestSpecification = anonymousRequest();
    }

    static Object[][] messages() {
        return new Object[][]{
            {"payloads/wita/v15/qeb/H_00859616_message.xml", "payloads/wita/v15/qeb/H_00859616_response.xml", "H_00859616"},
            {"payloads/wita/v15/abm/H_00854543_message.xml", "payloads/wita/v15/qeb/H_00859616_response.xml", "H_00859616"},
            {"payloads/wita/v15/qeb/H_00859616_message.xml", "payloads/wita/v15/abm/H_00854543_response.xml", "H_00854543"},
            {"payloads/wita/v15/erlm/H_00854639_message.xml", "payloads/wita/v15/erlm/H_00854639_response.xml", "H_00854639"}
        };
    }

    @ParameterizedTest
    @MethodSource("messages")
    void receiveMessage(String requestPath, String responsePath, String externalOrderId) throws IOException {
        File body = new File(Objects.requireNonNull(getClass().getClassLoader()
            .getResource(requestPath)).getFile());

        String responsePayload = FileTestUtil.readResourceContent(responsePath);

        Response response = given().spec(requestSpecification)
            .body(body)
            .post()
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        // TODO: Ignore whitespaces while asserting message
        inboxService.findByExternalOrderId(externalOrderId).forEach(item -> {
            assertThat(item)
                .isNotNull()
                .extracting(WitaProductInbox::getExternalOrderId, WitaProductInbox::getMessage)
                .containsExactly(externalOrderId, responsePayload);

        });
        // TODO: Assert response content
        response.prettyPrint();
    }

    @Test
    void receiveInvalidMessage() {
        File body = new File(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("payloads/wita/v15/invalid_message_request.xml")).getFile());

        given().spec(anonymousRequest())
            .body(body)
            .post()
            .then()
            .statusCode(400);
    }

    protected RequestSpecification anonymousRequest() {
        return new RequestSpecBuilder()
            .setContentType(ContentType.XML.getContentTypeStrings()[1])
            .setBaseUri(baseUri())
            .setBasePath(WS_BASE_PATH)
            .addHeader("SOAPAction", SOAP_ACTION)
            .build();
    }
}
