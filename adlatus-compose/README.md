# README

## Intro

[docker-comose.yml](docker-compose.yml) starts core system with next images:

- mongoDB
- configuration server
- WITA 15 mocks
- Resource order management ROM
- Ambassador
- Interface transformator

It exposes 8080 port for order manipulation
ROM -> Interface transformator -> WITA mocks

Example:

```http request

### Create order
POST http://localhost:8080/resourceOrder
Content-Type: application/json
x-custom-header: 1
x-api-key: 324

{
  "category": "string",
  "completionDate": "2023-06-03T19:15:10.574Z",
  "description": "string",
  "expectedCompletionDate": "2023-06-03T19:15:10.574Z",
  "externalId": "string",
  "name": "string",
  "orderDate": "2023-06-03T19:15:10.574Z",
  "orderType": "string",
  "priority": 0,
  "requestedCompletionDate": "2023-06-03T19:15:10.574Z",
  "requestedStartDate": "2023-06-03T19:15:10.574Z",
  "startDate": "2023-06-03T19:15:10.574Z",
  "state": "acknowledged",
  "externalReference": [
    {
      "id": "string",
      "entityType": "string",
      "owner": "string",
      "@baseType": "string",
      "@schemaLocation": "string",
      "@type": "string"
    }
  ],
  "note": [
    {
      "id": "string",
      "author": "string",
      "date": "2023-06-03T19:15:10.574Z",
      "text": "string",
      "@baseType": "string",
      "@schemaLocation": "string",
      "@type": "string"
    }
  ],
  "orderItem": [
    {
      "id": "string",
      "action": "string",
      "quantity": 0,
      "state": "acknowledged",
      "appointment": {
        "id": "string",
        "href": "string",
        "description": "string",
        "@baseType": "string",
        "@schemaLocation": "string",
        "@type": "string",
        "@referredType": "string"
      },
      "orderItemRelationship": [
        {
          "relationshipType": "string",
          "orderItem": {
            "itemId": "string",
            "resourceOrderHref": "string",
            "resourceOrderId": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string",
            "@referredType": "string"
          },
          "@baseType": "string",
          "@schemaLocation": "string",
          "@type": "string"
        }
      ],
      "resource": {
        "id": "string",
        "href": "string",
        "category": "string",
        "description": "string",
        "endOperatingDate": "2023-06-03T19:15:10.574Z",
        "name": "string",
        "resourceVersion": "string",
        "startOperatingDate": "2023-06-03T19:15:10.574Z",
        "administrativeState": "locked",
        "attachment": [
          {
            "id": "string",
            "href": "string",
            "attachmentType": "string",
            "description": "string",
            "isRef": true,
            "mimeType": "string",
            "name": "string",
            "url": "string",
            "size": {
              "amount": 1,
              "units": "string"
            },
            "validFor": {
              "endDateTime": "2023-06-03T19:15:10.575Z",
              "startDateTime": "2023-06-03T19:15:10.575Z"
            },
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string",
            "@referredType": "string"
          }
        ],
        "note": [
          {
            "id": "string",
            "author": "string",
            "date": "2023-06-03T19:15:10.575Z",
            "text": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string"
          }
        ],
        "operationalState": "enable",
        "place": {
          "id": "string",
          "href": "string",
          "name": "string",
          "role": "string",
          "@baseType": "string",
          "@schemaLocation": "string",
          "@type": "string",
          "@referredType": "string"
        },
        "relatedParty": [
          {
            "id": "string",
            "href": "string",
            "name": "string",
            "role": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string",
            "@referredType": "string"
          }
        ],
        "resourceCharacteristic": [
          {
            "id": "string",
            "name": "string",
            "valueType": "string",
            "characteristicRelationship": [
              {
                "id": "string",
                "relationshipType": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string"
              }
            ],
            "value": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string"
          }
        ],
        "resourceRelationship": [
          {
            "id": "string",
            "href": "string",
            "relationshipType": "string",
            "resourceRelationshipCharacteristic": [
              {
                "id": "string",
                "name": "string",
                "valueType": "string",
                "characteristicRelationship": [
                  {
                    "id": "string",
                    "relationshipType": "string",
                    "@baseType": "string",
                    "@schemaLocation": "string",
                    "@type": "string"
                  }
                ],
                "value": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string"
              }
            ],
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string"
          }
        ],
        "resourceSpecification": {
          "id": "string",
          "href": "string",
          "name": "string",
          "version": "string",
          "@baseType": "string",
          "@schemaLocation": "string",
          "@type": "string",
          "@referredType": "string"
        },
        "resourceStatus": "standby",
        "usageState": "idle",
        "@baseType": "string",
        "@schemaLocation": "string",
        "@type": "string",
        "@referredType": "string"
      },
      "resourceSpecification": {
        "id": "string",
        "href": "string",
        "name": "string",
        "version": "string",
        "@baseType": "string",
        "@schemaLocation": "string",
        "@type": "string",
        "@referredType": "string"
      },
      "@baseType": "string",
      "@schemaLocation": "string",
      "@type": "string"
    }
  ],
  "relatedParty": [
    {
      "id": "string",
      "href": "string",
      "name": "string",
      "role": "string",
      "@baseType": "string",
      "@schemaLocation": "string",
      "@type": "string",
      "@referredType": "string"
    }
  ],
  "@baseType": "string",
  "@schemaLocation": "string",
  "@type": "string"
}

```
