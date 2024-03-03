package de.bitconex.adlatus.wholebuy.provision.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.bitconex.adlatus.wholebuy.provision.it.util.WitaMockingUtil;
import de.bitconex.adlatus.common.persistence.WitaProductOutboxRepository;
import de.bitconex.adlatus.common.model.WitaProductOutbox;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.ObjectFactory;
import de.telekom.wholesale.oss.v15.order.AuftragType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.bitconex.adlatus.wholebuy.provision.it.WitaIT.WS_BASE_PATH;
import static de.bitconex.adlatus.wholebuy.provision.it.util.WitaMockingUtil.SOAP_ORDER_ACTION;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(initializers = {WireMockServerInitializer.class})
@Slf4j
class WitaOutboxIT extends IntegrationTestBase {

    @Autowired
    private WitaProductOutboxRepository witaOutboxRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() throws JAXBException, IOException {
        wireMockServer.resetAll();
        configureFor("localhost", wireMockServer.port());
        stubFor(WitaMockingUtil.stubOrderEndpoint());

        ObjectFactory objectFactory = new ObjectFactory();
        AnnehmenAuftragRequestType annehmenAuftragRequest = objectFactory.createAnnehmenAuftragRequestType();
        AuftragType auftrag = new AuftragType();
        auftrag.setExterneAuftragsnummer(UUID.randomUUID().toString());
        annehmenAuftragRequest.setAuftrag(auftrag);
        String message = MarshallUtil.marshall(annehmenAuftragRequest);

        WitaProductOutbox productOutbox = WitaProductOutbox.builder()
                .message(message)
                .status(WitaProductOutbox.Status.CREATED)
                .externalOrderId(UUID.randomUUID().toString())
                .retries(0)
                .build();

        witaOutboxRepository.save(productOutbox);
    }

    @Test()
    void checkOutbox() {
        Optional<WitaProductOutbox> createdWitaProductOutboxes = witaOutboxRepository.findFirstByStatusOrderByRetriesAsc(WitaProductOutbox.Status.CREATED);
        assertThat(createdWitaProductOutboxes).isPresent();
        processOutbox(createdWitaProductOutboxes.get());
    }

    private void processOutbox(WitaProductOutbox witaProductOutbox) {
        log.info("Message {}", witaProductOutbox.getMessage());

        Response response = given().spec(anonymousRequest())
                .body(witaProductOutbox.getMessage())
                .post()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        response.prettyPrint();

        witaProductOutbox.setStatus(WitaProductOutbox.Status.SENT);
        WitaProductOutbox save = witaOutboxRepository.save(witaProductOutbox);


        assertThat(save.getStatus()).isEqualTo(WitaProductOutbox.Status.SENT);
        assertThat(save.getMessage()).isEqualTo(witaProductOutbox.getMessage());
        assertThat(save.getExternalOrderId()).isEqualTo(witaProductOutbox.getExternalOrderId());
        assertThat(save.getId()).isEqualTo(witaProductOutbox.getId());
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
