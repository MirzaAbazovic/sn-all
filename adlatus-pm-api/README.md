# TMF632 Party Management REST API Implementation ![Version](https://img.shields.io/badge/Version-4.0.0-blue)

This repository contains the implementation of the TMF632 Party Management REST API using Java Spring Boot. The TMF632
standard, developed by the TeleManagement Forum (TM Forum), defines a set of APIs for managing party-related information
in a telecommunications environment. These APIs enable the creation, retrieval, updating, and deletion of information
related to parties, such as customers and organizations.

## Introduction

The TMF632 Party Management REST API Implementation is built using Java Spring Boot and adheres to the TMF632 standard.
It provides a set of RESTful endpoints that allow you to perform various operations related to party management within a
telecommunications context. This implementation is designed to be used as a starting point for building party management
solutions in the telecommunications industry. The TMF632 Party Management REST API Implementation is built using Java
Spring Boot and adheres to the TMF632 standard. The required files, request, and response structures for this
implementation are generated using the OpenAPI Generator, ensuring compliance with the TMF632 standard.

## Party Management

In the context of the TMF632 standard, "party" refers to entities such as customers, organizations, and
other entities that are relevant to the telecommunications industry. Party management involves the creation, retrieval,
updating, and deletion of information related to these parties.

Party management enables you to perform the following operations:

- **Retrieve Parties**: You can retrieve a list of parties or a specific party by their unique ID.

- **Create Parties**: Create new parties in the system, providing relevant information such as party type, contact
  details, and other attributes.

- **Update Parties**: Update existing parties by specifying their ID and providing the updated information.

- **Delete Parties**: Delete parties from the system by specifying their ID.

## Endpoints

The following is a list of available endpoints for this implementation:

- `GET /individual`: Retrieve a list of individuals.
- `GET /individual/{id}`: Retrieve a specific individual by ID.
- `POST /individual`: Create a new individual.
- `PATCH /individual/{id}`: Update an existing individual by ID.
- `DELETE /individual/{id}`: Delete an individual by ID.

---

- `GET /organization`: Retrieve a list of organizations.
- `GET /organization/{id}`: Retrieve a specific organization by ID.
- `POST /organization`: Create a new organization.
- `PATCH /organization/{id}`: Update an existing organization by ID.
- `DELETE /organization/{id}`: Delete an organization by ID.

Each endpoint may have specific request and response formats as defined by the TMF632 standard. You can refer to the API
documentation or the source code for detailed information about the request and response structures.

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

