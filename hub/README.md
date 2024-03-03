# TMF HUB

Implementation of subscription and notification pattern in TMF.
Lib adds REST endpoint for listeners to subscribe /hub.
Notification of listeners is based on event name. In TMF all listeners will be notified on /listener/{eventName}

## Usage

Add jar to pom.xml

```xml
  <dependency>
    <groupId>de.bitconex</groupId>
    <artifactId>hub</artifactId>
    <version>X.X.X</version>
</dependency>
```

Annotate to scan in de.bitconex.hub for components

```java
//add at class that contains main
@ComponentScan(basePackages = {"your-current-base-package", "de.bitconex.hub"})
public class AppointmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }
}
```

For Mongo registration service
```java
@EnableMongoRepositories
```

For JPA registration service
```java
@EnableJpaRepositories
```


Inject Notificator and use it to notify subscribers

```java

    private final Notificator notificator;

    public AppointmentApiController(Notificator notificator) {
        this.notificator = notificator;
    }
    //....
    //in some method/service
    NotificationRequest<AppointmentCreateEvent> notification = new NotificationRequest<>();
    AppointmentCreateEvent appointmentCreateEvent = new AppointmentCreateEvent();
    notification.setEvent(appointmentCreateEvent);
    NotificationResponse notificationResponse = notificator.notifyListener(notification);
```

Notification response is collections of notified subscribers id and HTTP response code.

To get all subscribers check HUB rest api.
```shell
curl http://localhost:8080/hub
```

## Development testing
Run Postgres database Docker container
```shell
docker run \
  -e POSTGRES_USER=test \
  -e POSTGRES_PASSWORD=test \
  -e POSTGRES_DB=hub_test_db \
  -p 5432:5432 \
  -d postgres
```

## Configuration

By default, hub uses in memory service. You can use mongoDB You will have to add mongodb

```xml
 <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

and to configure hub to use MongoDB service

```yml
spring:
  data:
    mongodb:
      database: hub
      port: '27017'
      host: localhost
hub:
  eventRegistrationService: EventRegistrationServiceMongo
```

```properties
hub.eventRegistrationService=EventRegistrationServiceMongo
spring.data.mongodb.database=events
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
```

Collection eventSubscription will be used to store data about subscribers.