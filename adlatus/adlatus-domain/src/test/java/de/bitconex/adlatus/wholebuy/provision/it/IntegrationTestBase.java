package de.bitconex.adlatus.wholebuy.provision.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import de.bitconex.hub.model.EventSubscription;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"mongo"})
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase extends IntegrationTestContainers {

    @LocalServerPort
    protected int port;

    protected final ObjectMapper objectMapper = JsonUtil.createObjectMapper();

    protected RequestSpecification requestSpecification; // should be set before each test

    private WireMockServer hubServer;

    String HUB_ENDPOINT = "/hub";

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        // mongodb config
        registry.add("spring.data.mongodb.uri", MONGODB_CONTAINER::getReplicaSetUrl);
        // api gateway config
        var host = API_GATEWAY_CONTAINER.getHost();
        var port = API_GATEWAY_CONTAINER.getMappedPort(AG_PORT);
        registry.add("rcm.api.basepath", () -> String.format("http://%s:%s/tmf-api/resourceCatalog/v4", host, port));
        registry.add("agreement.api.basepath", () -> String.format("http://%s:%s/tmf-api/agreementManagement/v4", host, port));
        registry.add("appointment.api.basepath", () -> String.format("http://%s:%s/tmf-api/appointment/v4", host, port));
        registry.add("pm.api.basepath", () -> String.format("http://%s:%s/tmf-api/party/v4", host, port));
        registry.add("ga.api.basepath", () -> String.format("http://%s:%s/tmf-api/geographicAddressManagement/v4", host, port));
    }

    @NotNull
    protected String baseUri() {
        return "http://localhost:" + port;
    }

    @Test
    void testContainers() {
        assertThat(MONGODB_CONTAINER.isRunning()).isTrue();
        assertThat(API_GATEWAY_CONTAINER.isRunning()).isTrue();
        assertThat(CONFIG_SERVER_CONTAINER.isRunning()).isTrue();
        assertThat(RCM_CONTAINER.isRunning()).isTrue();
        assertThat(AP_CONTAINER.isRunning()).isTrue();
        assertThat(PM_CONTAINER.isRunning()).isTrue();
        assertThat(GEO_ADDRESS_CONTAINER.isRunning()).isTrue();
    }


    protected RequestSpecification anonymousRequest() {
        final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setBaseUri(baseUri())
            .build();
        return requestSpecification;
    }

    public static String seedData(String baseUri, String jsonData, String endpoint) {
        Response response = RestAssured.given()
            .baseUri(baseUri)
            .contentType(ContentType.JSON)
            .body(jsonData)
            .when()
            .post(endpoint);

        // Check if the request was successful
        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Data seeding failed with status code: " + response.getStatusCode());
        }

        return response.body().asString();
    }

    @SneakyThrows
    protected void listenToHub(String q) {
        String query = q == null ? "ResourceOrderCreateEvent" : q;

        if (hubServer == null) {
            hubServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        }

        if (!hubServer.isRunning()) {
            hubServer.start();
            hubServer.stubFor(WireMock.post(urlPathMatching(String.format("/listener/%s", query)))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}")));

            hubServer.stubFor(any(anyUrl())
                .atPriority(10)
                .willReturn(aResponse()
                    .withStatus(500))
            );


        }

        var eventSubscription = EventSubscription.builder()
            .callback(hubServer.baseUrl())
            .query(query)
            .build();

        ObjectMapper objectMapper = JsonUtil.createObjectMapper();
        Response response = RestAssured.given()
            .baseUri(baseUri())
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(eventSubscription))
            .when()
            .post(HUB_ENDPOINT);

        // Check if the request was successful
        if (response.getStatusCode() / 100 != 2) {
            throw new RuntimeException("Subscribing to hub failed with status code: " + response.getStatusCode());
        }
    }

    protected void destroyHub() {
        hubServer.resetAll();
    }

    protected void verifyHub(String q) {
        hubServer.verify(postRequestedFor(urlPathMatching(String.format("/listener/%s", q))));
    }

}
