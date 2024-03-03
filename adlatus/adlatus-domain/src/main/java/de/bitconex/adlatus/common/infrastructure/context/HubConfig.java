package de.bitconex.adlatus.common.infrastructure.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "de.bitconex.hub"
})
public class HubConfig {
}
