package de.bitconex.tmf.resource_catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationCreate;
import de.bitconex.tmf.resource_catalog.service.ResourceSpecificationService;
import de.bitconex.tmf.resource_catalog.utility.JsonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoadRunner implements CommandLineRunner {
    private Boolean loadInitialData= Boolean.valueOf(System.getenv("initial_load"));
    private final ResourceSpecificationService resourceSpecificationService;

    private ObjectMapper mapper = JsonUtil.createObjectMapper();

    public InitialDataLoadRunner(ResourceSpecificationService resourceSpecificationService) {
        this.resourceSpecificationService = resourceSpecificationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (loadInitialData) {
            ClassPathResource agreementResource = new ClassPathResource("initial_data/catalogs.json");
            ResourceSpecificationCreate resourceSpecificationCreate = mapper.readValue(agreementResource.getInputStream(), ResourceSpecificationCreate.class);
            resourceSpecificationService.createResourceSpecification(resourceSpecificationCreate);
        }
    }
}
