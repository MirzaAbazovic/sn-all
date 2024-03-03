package de.bitconex.agreement;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.service.AgreementService;
import de.bitconex.agreement.util.JsonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoadRunner implements CommandLineRunner {
    private Boolean loadInitialData= Boolean.valueOf(System.getenv("initial_load"));
    private AgreementService agreementService;

    private ObjectMapper mapper = JsonUtil.createObjectMapper();

    public InitialDataLoadRunner(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (loadInitialData) {
            ClassPathResource agreementResource = new ClassPathResource("initial_data/agreements.json");
            AgreementCreate agreementCreate = mapper.readValue(agreementResource.getInputStream(), AgreementCreate.class);
            agreementService.createAgreement(agreementCreate);
        }
    }
}
