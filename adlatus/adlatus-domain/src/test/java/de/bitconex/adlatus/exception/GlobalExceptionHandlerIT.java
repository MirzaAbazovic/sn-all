package de.bitconex.adlatus.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bitconex.adlatus.wholebuy.provision.it.IntegrationTestBase;
import de.bitconex.adlatus.wholebuy.provision.it.util.ResourceCatalogTestUtil;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GlobalExceptionHandlerIT extends IntegrationTestBase {

    public static final String RESOURCE_ORDER_BASE_PATH = "/resourceOrder";

    private ResourceSpecification createdResourceSpecification;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        requestSpecification = anonymousRequest();

        ResourceSpecification resSpec = ResourceCatalogTestUtil.createResourceSpecification();

        createdResourceSpecification = objectMapper.readValue(
                seedData(
                        String.format("http://%s:%s", RCM_CONTAINER.getHost(), RCM_CONTAINER.getMappedPort(RCM_PORT)),
                        objectMapper.writeValueAsString(resSpec),
                        "/resourceSpecification"),
                ResourceSpecification.class
        );
    }

    @Test
    public void whenCreateOrderInvalidException_thenReturnsBadRequest() throws JsonProcessingException {
        // Create an invalid ResourceOrderCreate object
        final ResourceOrderCreate resourceOrderCreate = new ResourceOrderCreate();

        // Convert the object to a JSON string
        String body = null;
        body = objectMapper.writeValueAsString(resourceOrderCreate);

        // Send a POST request to create a resource order
        // Expect a 400 Bad Request response
        given()
                .spec(requestSpecification)
        .when()
                .body(body)
                .post(RESOURCE_ORDER_BASE_PATH)
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Invalid resource order."));
    }
}