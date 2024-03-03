package de.bitconex.adlatus.wholebuy.provision.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bitconex.adlatus.wholebuy.provision.it.util.ResourceCatalogTestUtil;
import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogService;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceCatalogServiceIT extends IntegrationTestBase {

    @Autowired
    private ResourceCatalogService resourceCatalogService;

    private ResourceSpecification createdResourceSpecification;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        requestSpecification = anonymousRequest();
        createdResourceSpecification = null;
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
    void retrieveSpecification() {
        String id = createdResourceSpecification.getId();

        ResourceSpecification resourceSpecification = resourceCatalogService.retrieveSpecification(id);

        assertNotNull(resourceSpecification);
        assertNotNull(resourceSpecification.getId());
        assertEquals(id, resourceSpecification.getId());
        assertThat(resourceSpecification)
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields(
                        "lastUpdate"
                )
                .isEqualTo(createdResourceSpecification);
    }
}