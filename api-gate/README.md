# Gateway API for Adlatus

## Intro

Gateway API (Gate) is used that all UI app have one point to use all TMFs rest apis.
Gate is used to collect all REST TMF, and non TMF APIs, he proxies request to services behind.

Roles of Gate are
- routing,
- SSL termination and
- versioning (both by url and headers)


Convention for TMF APIs:
/tmf-api/v{major-version}/{tmf-module}
for example:

/tmf-api/party/v4
/tmf-api/resourceCatalog/v4

Versioning for minor and patch parts of the version is done using headers:

GET /tmf-api/resourceCatalog/v4
Accept: application/vnd.example.resource+json;version=4.1.0

GET /tmf-api/resourceCatalog/v4
Accept: application/vnd.example.resource+json;version=4.3.2


## Run

```shell
gradle :bootRun
```

## Docker

You can build image with name gate using

```shell
gradle jib --image=gate
```

To see configured routes You can use /actuator/gateway/routes or actuator/gateway/routedefinitions


- https://localhost/actuator/gateway/routes
- https://localhost/actuator/gateway/routedefinitions

