package de.bitconex.tmf.resource_catalog;

import de.bitconex.tmf.resource_catalog.utility.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourceCatalogApplication implements CommandLineRunner {
    @Autowired
    private DataLoaderService dataLoaderService;

//    @Value("${seed.data.enabled}")
//    private boolean seedDataEnabled;

    public static void main(String[] args) {
        SpringApplication.run(ResourceCatalogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        if (seedDataEnabled)
//            dataLoaderService.loadData();

        dataLoaderService.createGenericData();
    }
}
