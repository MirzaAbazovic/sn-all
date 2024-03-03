package de.bitconex.adlatus.wholebuy.provision.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bitconex.adlatus.wholebuy.provision.it.util.ResourceCatalogTestUtil;
import de.bitconex.adlatus.wholebuy.provision.it.util.ResourceOrderTestUtil;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.common.persistence.TmfOrderInboxRepository;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ResourceOrderApiIT extends IntegrationTestBase {

    public static final String RESOURCE_ORDER_BASE_PATH = "/resourceOrder";

    @Autowired
    private TmfOrderInboxRepository tmfOrderInboxRepository;

    private ResourceSpecification createdResourceSpecification;

    OffsetDateTime NOW = OffsetDateTime.now();

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
    void testCreateOrder() throws JsonProcessingException {
        final ResourceOrderCreate resourceOrderCreate = ResourceOrderTestUtil.createResourceOrderCreate(createdResourceSpecification);

        final ResourceOrder expectedResourceOrder = new ResourceOrder();
        expectedResourceOrder.setOrderItem(resourceOrderCreate.getOrderItem());
        expectedResourceOrder.setName("order-name");
        expectedResourceOrder.setState("acknowledged");

        String body = objectMapper.writeValueAsString(resourceOrderCreate);
        ResourceOrder resourceOrder = given().spec(requestSpecification)
            .when().body(body)
            .post(RESOURCE_ORDER_BASE_PATH)
            .then().statusCode(HttpStatus.CREATED.value())
            .extract().as(ResourceOrder.class);

        assertThat(resourceOrder)
            .usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .ignoringFields("id", "href", "orderDate")
            .isEqualTo(expectedResourceOrder);

        assertThat(resourceOrder.getId()).isNotBlank();
        assertThat(resourceOrder.getHref()).isNotBlank();
        assertThat(resourceOrder.getHref()).isEqualTo(String.format("resourceOrder/%s", resourceOrder.getId()));

        var orderInDatabase = tmfOrderInboxRepository.findByOrderId(resourceOrder.getId()).orElseThrow(() -> new RuntimeException("Order not found in database"));
        assertThat(orderInDatabase)
            .isNotNull()
            .extracting(TmfOrderInbox::getOrderId,
                TmfOrderInbox::getStatus,
                TmfOrderInbox::getMessage)
            .containsExactly(resourceOrder.getId(),
                Status.ACKNOWLEDGED,
                objectMapper.writeValueAsString(resourceOrder));

    }

    @Test
    void testCreateOrderBadRequest() throws JsonProcessingException {
        final ResourceOrderCreate resourceOrderCreate = new ResourceOrderCreate();
        given().spec(requestSpecification)
            .when()
            .body(objectMapper.writeValueAsString(resourceOrderCreate))
            .post(RESOURCE_ORDER_BASE_PATH)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
