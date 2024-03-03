package de.bitconex.adlatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AdlatusDomainApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdlatusDomainApplication.class, args);
    }
}


