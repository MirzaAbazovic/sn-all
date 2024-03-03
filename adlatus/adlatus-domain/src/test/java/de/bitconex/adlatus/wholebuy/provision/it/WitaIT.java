package de.bitconex.adlatus.wholebuy.provision.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import de.bitconex.adlatus.wholebuy.provision.it.util.WitaMockingUtil;
import de.bitconex.adlatus.wholebuy.provision.dto.constants.Constants;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.ObjectFactory;
import de.telekom.wholesale.oss.v15.order.AuftragType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.bitconex.adlatus.wholebuy.provision.it.util.WitaMockingUtil.SOAP_ORDER_ACTION;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(initializers = {WireMockServerInitializer.class})
public class WitaIT extends IntegrationTestBase {

    public static String WS_BASE_PATH = "/ws";

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private TmfOrderInboxService tmfOrderInboxService;

    @BeforeEach
    void setUp() throws IOException {
        wireMockServer.resetAll();
        configureFor("localhost", wireMockServer.port());
        stubFor(WitaMockingUtil.stubOrderEndpoint());

        requestSpecification = anonymousRequest();

        String orderId = UUID.randomUUID().toString();
        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id(orderId)
            .href(String.format("%s/%s", Constants.TMF_RESOURCE_ORDER_URL_PREFIX, orderId))
            .build();

        tmfOrderInboxService.save(
            TmfOrderInbox.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .message(JsonUtil.createObjectMapper().writeValueAsString(resourceOrder))
                .status(Status.ACKNOWLEDGED)
                .build()
        );
    }

    @Test
    void sendOrder() throws JsonProcessingException, JAXBException {
        TmfOrderInbox acknowledgedOrder = tmfOrderInboxService.findFirstByStatus(Status.ACKNOWLEDGED);

        AnnehmenAuftragRequestType body = transformToWita(JsonUtil.createObjectMapper()
            .readValue(acknowledgedOrder.getMessage(), ResourceOrder.class));

        Response response = given().spec(anonymousRequest())
            .body(MarshallUtil.marshall(body))
            .post()
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        System.out.println(response.prettyPrint());

        assertThat(response)
            .isNotNull();

    }

    private AnnehmenAuftragRequestType transformToWita(ResourceOrder resourceOrder) {
        ObjectFactory objectFactory = new ObjectFactory();
        AnnehmenAuftragRequestType annehmenAuftragRequest = objectFactory.createAnnehmenAuftragRequestType();
        AuftragType auftrag = new AuftragType();
        auftrag.setExterneAuftragsnummer(resourceOrder.getId());
        annehmenAuftragRequest.setAuftrag(auftrag);
        return annehmenAuftragRequest;
    }

    protected RequestSpecification anonymousRequest() {
        return new RequestSpecBuilder()
            .setContentType(ContentType.XML.getContentTypeStrings()[1])
            .setBaseUri("http://localhost")
            .setPort(wireMockServer.port())
            .setBasePath(WS_BASE_PATH)
            .addHeader("Accept", MediaType.TEXT_XML_VALUE)
            .addHeader("SOAPAction", SOAP_ORDER_ACTION)
            .build();
    }
}
