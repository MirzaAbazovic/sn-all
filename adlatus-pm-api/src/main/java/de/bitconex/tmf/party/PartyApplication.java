package de.bitconex.tmf.party;

import de.bitconex.tmf.party.models.Organization;
import de.bitconex.tmf.party.repository.OrganizationRepository;
import de.bitconex.tmf.party.utility.HrefTypes;
import de.bitconex.tmf.party.utility.ModelUtils;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PartyApplication implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;

    public PartyApplication(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PartyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var provider = new Organization();
        provider.setName("Provider 1");
        provider.setTradingName("Provider 1");
        provider.setId(new ObjectId("000000000000000000000000").toString());
        ModelUtils.setIdAndHref(provider, HrefTypes.Organization.getHrefType());
        organizationRepository.save(provider);

        var consumer = new Organization();
        consumer.setName("Consumer 1");
        consumer.setTradingName("Consumer 1");
        consumer.setId(new ObjectId("000000000000000000000001").toString());
        ModelUtils.setIdAndHref(consumer, HrefTypes.Organization.getHrefType());
        organizationRepository.save(consumer);
    }
}
