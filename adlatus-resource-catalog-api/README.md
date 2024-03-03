# TMF 634 Resource Catalog Management API ![Version](https://img.shields.io/badge/Version-4.1.0-blue)

This repository contains the implementation of the TMF 634 Resource Catalog Management API. The TMF 634 standard, developed by the TeleManagement Forum (TM Forum), defines a set of APIs for managing resource catalogs, resource categories, resource candidates, resource specifications, import jobs, and export jobs within a telecommunications environment. These APIs enable the retrieval, creation, update, and deletion of resource-related data.

## Introduction

The TMF 634 Resource Catalog Management API is designed to facilitate the management of various resource-related data and job operations within a telecommunications context. It provides a set of endpoints that allow users to interact with resource catalogs, categories, candidates, specifications, import jobs, and export jobs. This README provides an overview of the Resource Catalog Management API and its available endpoints.

[OpenAPI v4.0.0](https://tmf-open-api-table-documents.s3.eu-west-1.amazonaws.com/OpenApiTable/4.1.0/swagger/TMF634-ResourceCatalog-v4.0.0.swagger.json)

[User Guide pdf](https://tmf-open-api-table-documents.s3.eu-west-1.amazonaws.com/OpenApiTable/4.1.0/user_guides/TMF634_Resource_Catalog_Management_API_User_Guide_v4.1.0.pdf)

## Resource Catalog

In the context of the TMF 634 standard, the "Resource Catalog" is a key component that stores information about resources, their categories, candidates, and specifications. The API offers functionalities for managing these resource-related elements:

- **Resource Catalogs**: Allows users to retrieve a list of resource catalogs or details about a specific catalog by its unique ID. Users can create, update, and delete catalogs.

- **Resource Categories**: Permits the retrieval of resource categories and their details. Users can create, update, and delete resource categories.

- **Resource Candidates**: Enables the management of resource candidates, including retrieval, creation, updating, and deletion.

- **Resource Specifications**: Provides functionalities for managing resource specifications, including retrieval, creation, updating, and deletion.

- **Import Jobs**: Allows users to manage import jobs, such as retrieving job information, creating new import jobs, and deleting import jobs.

- **Export Jobs**: Permits users to manage export jobs, including retrieving job information, creating new export jobs, and deleting export jobs.

## Endpoints

The following is a list of available endpoints for the TMF 634 Resource Catalog Management API:

### Resource Catalog

- `GET /resourceCatalog`: Retrieve a list of resource catalogs.
- `GET /resourceCatalog/{id}`: Retrieve a specific resource catalog by ID.
- `POST /resourceCatalog`: Create a new resource catalog.
- `PATCH /resourceCatalog/{id}`: Update an existing resource catalog by ID.
- `DELETE /resourceCatalog/{id}`: Delete a resource catalog by ID.

### Resource Category

- `GET /resourceCategory`: Retrieve a list of resource categories.
- `GET /resourceCategory/{id}`: Retrieve a specific resource category by ID.
- `POST /resourceCategory`: Create a new resource category.
- `PATCH /resourceCategory/{id}`: Update an existing resource category by ID.
- `DELETE /resourceCategory/{id}`: Delete a resource category by ID.

### Resource Candidate

- `GET /resourceCandidate`: Retrieve a list of resource candidates.
- `GET /resourceCandidate/{id}`: Retrieve a specific resource candidate by ID.
- `POST /resourceCandidate`: Create a new resource candidate.
- `PATCH /resourceCandidate/{id}`: Update an existing resource candidate by ID.
- `DELETE /resourceCandidate/{id}`: Delete a resource candidate by ID.

### Resource Specification

- `GET /resourceSpecification`: Retrieve a list of resource specifications.
- `GET /resourceSpecification/{id}`: Retrieve a specific resource specification by ID.
- `POST /resourceSpecification`: Create a new resource specification.
- `PATCH /resourceSpecification/{id}`: Update an existing resource specification by ID.
- `DELETE /resourceSpecification/{id}`: Delete a resource specification by ID.

### Import Job

- `GET /importJob`: Retrieve a list of import jobs.
- `GET /importJob/{id}`: Retrieve a specific import job by ID.
- `POST /importJob`: Create a new import job.
- `DELETE /importJob/{id}`: Delete an import job by ID.

### Export Job

- `GET /exportJob`: Retrieve a list of export jobs.
- `GET /exportJob/{id}`: Retrieve a specific export job by ID.
- `POST /exportJob`: Create a new export job.
- `DELETE /exportJob/{id}`: Delete an export job by ID.

Each endpoint may have specific request and response formats as defined by the TMF 634 standard. Users can refer to the API documentation or inspect the source code for detailed information about the request and response structures.
These endpoints are added to API base path found in `application.properties`

Prerequisites:
1. Run Docker
2. Install MongoDB:
   CMD/PowerShell: docker pull mongo:latest
   CMD/PowerShell: docker run -d -p 27017:27017 --name=mongo-example mongo:latest
   CMD/PowerShell: docker ps or open Docker->Containers and see if MongoDB is running

Start your server as an simple java application

You can view the api documentation in swagger-ui by pointing to  
http://localhost:8080/

Change default port value in application.properties