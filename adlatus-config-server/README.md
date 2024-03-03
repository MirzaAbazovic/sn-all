# Adlatus Config Server

The **Adlatus Config Server** is a centralized configuration management tool built using Spring Boot and Spring Cloud Config. It allows the manage and distribute of configuration properties for various subsystems, all from a Git repository
https://bitbucket.org/bitconex/adlatus-configuration/src/master/


Runs on port 8888

```shell
mvn spring-boot:run
```

Using docker/podman
```shell
docker run -d -p 8888:8888 --name=config-server bitconex-de.registry.jetbrains.space/p/main/bitconex/config-server:latest
````
or podman
```shell
podman run -d -p 8888:8888 --name=config-server bitconex-de.registry.jetbrains.space/p/main/bitconex/config-server:latest
```

Check is it up by querying config of one app for example 'resource-order-management':
```shell
curl http://localhost:8888/resource-order-management/default
```
Or go to http://localhost:8888/resource-order-management/default for default profile or http://localhost:8888/resource-order-management/dockcomp for dockcomp profile.

It will get info from this git repo:
https://bitbucket.org/bitconex/adlatus-configuration/src/master/default/resource-order-management/application.properties

Config repo is grouped by profiles, and in each profile there is folder named by application name.

Used branch is always master, and if You commit change on this branch the config server will reconfigure app in real time and provide new configuration data.
