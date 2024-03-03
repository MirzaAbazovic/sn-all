# TMF 646 Appointment REST API Implementation ![Version](https://img.shields.io/badge/Version-4.0.0-blue)

This repository contains the implementation of the TMF 646 Appointment REST API using Java Spring Boot. The TMF 646 standard, developed by the TeleManagement Forum (TM Forum), defines a set of APIs for managing appointments and scheduling within a telecommunications environment. These APIs enable the creation, retrieval, updating, and cancellation of appointments for various purposes, such as service provisioning and customer appointments.

## Introduction

The TMF 646 Appointment REST API Implementation is built using Java Spring Boot and adheres to the TMF 646 standard. The required files, request, and response structures for this implementation are generated using the OpenAPI Generator, ensuring compliance with the TMF 646 standard.

## Appointment Management API

In the context of the TMF 646 standard, the "Appointment Management API" allows telecommunications service providers to efficiently manage appointments for various purposes. These purposes can include scheduling technician visits, customer appointments for service activation or repair, and other time-sensitive activities.

This API provides the following key functionalities:

- **Retrieve Appointments**: Allows the retrieval of a list of appointments and details about specific appointments using their unique IDs.

- **Create Appointments**: Permits the creation of new appointments by providing relevant information

- **Update Appointments**: Allows for the modification of existing appointments

- **Delete Appointments**: Provides the capability to cancel appointments by specifying their ID

---

- **Retrieve Search Time Slot**: Allows the retrieval of a list of search time slots and details about specific search time slots using their unique IDs.

- **Create Search Time Slot**: Permits the creation of new Search Time Slots by providing relevant information

- **Update Search Time Slots**: Allows for the modification of existing search time slots

- **Delete Search Time Slots**: Provides the capability to delete search time slots by specifying their ID

These features enable service providers to efficiently schedule and manage appointments, improving customer service and resource allocation.


## Endpoints

The following is a list of available endpoints for this Spring Boot implementation:

- `GET /appointment`: Retrieve a list of appointments.
- `GET /appointment/{id}`: Retrieve a specific appointment by ID.
- `POST /appointment`: Create a new appointment.
- `PATCH /appointment/{id}`: Update an existing appointment by ID.
- `DELETE /appointment/{id}`: Cancel (delete) an appointment by ID.
---
- `GET /searchTimeSlot`: Search for available time slots based on specified criteria
- `POST /searchTimeSlot`: Create a new time slot search query
- `PATCH /searchTimeSlot/{id}`: Update an existing time slot search query by ID
- `DELETE /searchTimeSlot/{id}`: Delete a time slot search query by ID

Each endpoint may have specific request and response formats as defined by the TMF 646 standard. You can refer to the API documentation or the source code for detailed information about the request and response structures.
These endpoints are added to API base path found in `application.properties`
