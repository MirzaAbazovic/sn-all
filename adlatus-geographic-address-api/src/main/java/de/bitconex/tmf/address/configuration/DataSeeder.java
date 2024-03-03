package de.bitconex.tmf.address.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.address.repository.GeographicAddressRepository;
import de.bitconex.tmf.address.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    GeographicAddressRepository repository;

    @Override
    public void run(String... args) throws Exception {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/seed_data.json");
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found! " + "seed_data.json");
            } else {
                var objectMapper = JsonUtil.getObjectMapper();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String fileContent = stringBuilder.toString();

                List<GeographicAddress> geographicAddresses = objectMapper.readValue(fileContent, new TypeReference<>(){});
                repository.saveAll(geographicAddresses);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
