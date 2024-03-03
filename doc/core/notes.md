# Notes

## Forum

https://engage.tmforum.org/discussion/where-can-i-find-out-more-about-how-to-think-about-tmf-641-vs-652

TM Forum (in line with general industry practice) makes a distinction between Service and Resource. This has been expressed in the Information Framework (SID) for many years already, and as a result is also reflected in the Open API model. We have the parallel API sets:

- Service Catalog (TMF633), Service Order (TMF641), Service Activation (TMF640), Service Inventory (TMF638)
Resource Catalog (TMF634), 

- Resource Order (TMF652), Resource Activation (TMF702), Resource Inventory (TMF639)
A very short explanation (bear in mind that I am not an OSS expert :) ):

Service - is how the communication (or other) service is implemented in the network. For example, a Broadband product might be implemented for a customer in the network as a fiber connection between the home and the central office; this is the Service.

Resource - is something that is used to implement the service; could be a physical resource (like the actual fiber cable, or the home router), or a logical resource (like a static IP address)


## End to end ODA


End to End ODA interesting flow and orchestration of services to fulfill some business use case in Telco
- [TMSF008 Use Case: Service and Resource Order Management for Postpaid Mobile Subscribers v3.0.1 (E2E-513)](
https://projects.tmforum.org/wiki/pages/viewpage.action?pageId=273482093)
- [TMSF011 Use Case: Order Fallout Management v5.0.1 (E2E-514)](https://projects.tmforum.org/wiki/pages/viewpage.action?pageId=273482138)


## State of order

The following table provides definition:

Acknowledged: The Acknowledged state is where an order/item has been received and has passed message and basic business validations.

Rejected: 

The Rejected state is where:

- An order failed the Order Feasibility check
- Invalid information is provided through the order request
- The order request fails to meet business rules for ordering
- Same rules applied for order item.

Pending: The Pending state is used when an order/item is currently in a waiting stage for an action/activity to be completed before the order/item can progress further, pending order amend or cancel assessment. In situations where Access Seeker action is required, an “information required” notification will be issued on transition into this state.

A pending stage can lead into auto cancellation of an order/item, if no action is taken within the defined timeframes to be described under the Agreement.

InProgress: The In Progress state is where an order Item has passed the Order Feasibility check successfully and product delivery has started.

For the order at least one order item is inProgress

Held: The Held state is used when an order/item cannot be progressed due to an issue. SP has temporarily delayed completing an order/item to resolve an infrastructure shortfall to facilitate supply of order. Upon resolution of the issue, the order/item will continue to progress.

AssessingCancellation: Following a cancel request, the SP is assessing if cancel can be done for the order/item (or if the PO has reached PONR). If cancellation request is not accepted after assessment, the order will return in Held or Pending or InProgress state.

PendingCancellation: Once a cancel order has been accepted by SP, it could in some UC take time to effectively cancel the order/item. During this time when accepted cancellation is effectively processed we use Pending Cancellation.

Cancelled: The Cancelled state is where an In-Flight Order/item has been successfully cancelled.

Completed: The Completed state is where an item has complete provision and the service is now active.

For an order all order item are completed

Failed: Order item as not a successful delivery completion. The product is not delivered and the order item failed.

All Order items have failed which results in the entire Order has Failed.

Partial: Some Order items have failed and some have succeeded, so the entire Order is in a Partial state. This provides support for partial Failure of an Order.

This state is not available at item level

## Mix

https://github.com/tmforum-rand/Open_Api_And_Data_Model-latest/tree/master/apis/TMF634_Resource_Catalog/samples

https://github.com/tmforum-rand/Open_Api_And_Data_Model-latest/blob/master/apis/TMF634_Resource_Catalog/swaggers/TMF634-ResourceCatalog-v4.1.0.swagger.json

https://github.com/tmforum-rand/Open_Api_And_Data_Model-latest/blob/master/schemas/Resource/Resource.schema.json