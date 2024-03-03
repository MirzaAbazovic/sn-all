package de.bitconex.adlatus.wholebuy.provision.it;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IntegrationTestContainers {
    // networks
    protected static final Network ADLATUS_NETWORK;

    // containers
    protected static final MongoDBContainer MONGODB_CONTAINER;
    protected static final GenericContainer<?> API_GATEWAY_CONTAINER;
    protected static final GenericContainer<?> CONFIG_SERVER_CONTAINER;
    protected static final GenericContainer<?> RCM_CONTAINER;
    protected static final GenericContainer<?> AP_CONTAINER;
    protected static final GenericContainer<?> PM_CONTAINER;
    protected static final GenericContainer<?> GEO_ADDRESS_CONTAINER;

    // List of all containers
    private static final List<GenericContainer<?>> CONTAINERS;

    // mongodb config
    protected static final int MONGO_PORT = 27017;
    protected static final String MONGO_ALIAS = "mongodb";

    // api gateway config
    protected static final int AG_PORT = 80;
    protected static final String AG_KEY_STORE_PASS = "matematika";

    // config server config
    protected static final int CS_PORT = 8080;
    protected static final String CS_ALIAS = "config-server";
    protected static final String CONFIGURATION_GIT_TOKEN = "ATCTT3xFfGN00xtKqlqFChbG1mmhktz7900yQ57M41eM6NlrtHPu_O_1ZBSwWDaFHe46PVzDWI7JDU1XW0U2lncZ4xUabpuOLmB_yeci7xmgEgsHzK9HAfJZR_vQq5-EnH5OGrXNQW5EyFIq1NGd94Uf40AipfeQgATWLlGcuXZPbIPQrpzF_Gk=1E43E8E7";

    // resource catalog management config
    protected static final int RCM_PORT = 8080;
    protected static final String RCM_ALIAS = "resource-catalog";

    // appointment config
    protected static final int AP_PORT = 8080;
    protected static final String AP_ALIAS = "appointment-management";

    // party management config
    protected static final int PM_PORT = 8080;
    protected static final String PM_ALIAS = "party-management";

    // geo address config
    protected static final int ADDRESS_PORT = 8080;
    protected static final String ADDRESS_ALIAS = "geographic-address-management";

    // Containers setup
    static {
        // common config
        final String SPRING_PROFILES_ACTIVE = "dockcomp";
        final String CONFIG_SERVER_URL = String.format("http://%s:%s", CS_ALIAS, CS_PORT);
        final Map<String, String> COMMON_CONFIG = Map.of(
            "SPRING_PROFILES_ACTIVE", SPRING_PROFILES_ACTIVE,
            "CONFIG_SERVER", CONFIG_SERVER_URL
        );

        // Network
        ADLATUS_NETWORK = Network.newNetwork();

        // MongoDB container
        MONGODB_CONTAINER = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(MONGO_PORT)
            .withNetwork(ADLATUS_NETWORK)
            .withNetworkAliases(MONGO_ALIAS)
            // Run the MongoDB server with a replica set named "rs0". A replica set is a group of MongoDB instances that maintain the same data set. Replica sets provide redundancy and high availability, and are the basis for all production deployments. In this case, it is used to be able to do transactions.
            .withCommand("--replSet", "rs0", "--bind_ip_all", "--port", "27017")
            // Add an entry to the container's /etc/hosts file, mapping "host.docker.internal" to the IP address of the host machine's gateway. This allows the container to communicate with the host machine.
            .withExtraHost("host.docker.internal", "host-gateway")
            .withStartupAttempts(3)
            .waitingFor(
                // Wait for the MongoDB server to be ready before proceeding. This command checks the status of the replica set and initiates it if necessary.
                Wait.forSuccessfulCommand("echo \"try { rs.status() } catch (err) { rs.initiate({_id:'rs0',members:[{_id:0,host:'host.docker.internal:27017'}]}) }\" | mongosh --port 27017 --quiet")
            );

        // Config Server container
        CONFIG_SERVER_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-config-server:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withNetworkAliases(CS_ALIAS)
            .withEnv(
                Map.of(
                    "PORT", "8080",
                    "GIT_TOKEN", CONFIGURATION_GIT_TOKEN
                )
            )
            .withExposedPorts(CS_PORT)
            .waitingFor(
                Wait.forHttp("/actuator/health")
                    .forStatusCode(200)
            );

        // API Gateway container
        API_GATEWAY_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-api-gate:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withExposedPorts(AG_PORT)
            .withEnv(
                new HashMap<>(COMMON_CONFIG) {{
                    put("KEY_STORE_PASS", AG_KEY_STORE_PASS);
                }}
            )
            .waitingFor(
                Wait.forHttp("/actuator/health")
                    .forStatusCode(200)
            )
            .dependsOn(CONFIG_SERVER_CONTAINER);

        // Resource Catalog Management container
        RCM_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-resource-catalog-api:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withExposedPorts(RCM_PORT)
            .withNetworkAliases(RCM_ALIAS)
            .withEnv(
                new HashMap<>(COMMON_CONFIG)
            )
            .dependsOn(CONFIG_SERVER_CONTAINER, MONGODB_CONTAINER);


        // Appointment container
        AP_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-appointment-api:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withExposedPorts(AP_PORT)
            .withNetworkAliases(AP_ALIAS)
            .withEnv(
                new HashMap<>(COMMON_CONFIG)
            )
            .dependsOn(CONFIG_SERVER_CONTAINER, MONGODB_CONTAINER);

        // Party Management container
        PM_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-pm-api:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withExposedPorts(PM_PORT)
            .withNetworkAliases(PM_ALIAS)
            .withEnv(
                new HashMap<>(COMMON_CONFIG)
            )
            .dependsOn(CONFIG_SERVER_CONTAINER, MONGODB_CONTAINER);

        GEO_ADDRESS_CONTAINER = new GenericContainer<>("docker.adl.dev.bitconex.com/bitconex-geographic-address-api:latest")
            .withNetwork(ADLATUS_NETWORK)
            .withExposedPorts(ADDRESS_PORT)
            .withNetworkAliases(ADDRESS_ALIAS)
            .withEnv(
                new HashMap<>(COMMON_CONFIG)
            )
            .dependsOn(CONFIG_SERVER_CONTAINER, MONGODB_CONTAINER);


        // List of all containers
        CONTAINERS = Arrays.asList(
            MONGODB_CONTAINER,
            API_GATEWAY_CONTAINER,
            CONFIG_SERVER_CONTAINER,
            RCM_CONTAINER,
            AP_CONTAINER,
            PM_CONTAINER,
            GEO_ADDRESS_CONTAINER
        );

        Startables.deepStart(CONTAINERS)
            .join();
    }
}

