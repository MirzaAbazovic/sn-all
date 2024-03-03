package de.bitconex.tmf.party;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.tmf.party.models.IndividualCreate;
import de.bitconex.tmf.party.service.IndividualService;
import de.bitconex.tmf.party.utility.JsonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitialDataLoadRunner implements CommandLineRunner {
    private Boolean loadInitialData= Boolean.valueOf(System.getenv("initial_load"));
    private final IndividualService individualService;

    private ObjectMapper mapper = JsonUtil.createObjectMapper();

    public InitialDataLoadRunner(IndividualService individualService) {
        this.individualService = individualService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (loadInitialData) {
            ClassPathResource agreementResource = new ClassPathResource("initial_data/individuals.json");
             List<IndividualCreate> individualCreateList = mapper.readValue(agreementResource.getInputStream(), new TypeReference<>(){});
            individualCreateList.forEach(individualCreate -> individualService.createIndividual(individualCreate));
        }
    }
}
