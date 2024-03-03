 ≥# Events in RCM, ROM and RIM


All events in:

## Resource catalog events

1. Resource Catalog Create Event
2. Resource Catalog Change Event
3. Resource Catalog Delete Event
4. Resource Category Create Event
5. Resource Category Change Event
6. Resource Category Delete Event
7. Resource Candidate Create Event
8. Resource Candidate Change Event
9. Resource Candidate Delete Event
10. Resource Specification Create Event
11. Resource Specification Change Event
12. Resource Specification Delete Event

## Resource order mgm. events

1. Resource Order Create Event
2. Resource Order Attribute Value Change Event
3. Resource Order State Change Event
4. Resource Order Delete Event
5. Resource Order Information Required Event
6. Cancel Resource Order Create Event
7. Cancel Resource Order State Change Event
8. Cancel Resource Order Information Required Event

## Resource inverntory events

1. Resource Create Event
2. Resource Attribute Value Change Event
3. Resource State Change Event
4. Resource Delete Event

Hub call on provided host /listener/{eventName} where event name is lower camel case of above listed events.


By looking at https://raw.githubusercontent.com/tmforum-apis/TMF688-Event/master/TMF688-Event-v4.0.0.swagger.json
I get hint that catalog, inventory and rom are modeled as topic which have events.

[Here](https://engage.tmforum.org/communities/community-home/digestviewer/viewthread?MessageKey=dff1a69b-18ab-47eb-97f4-d63803937234&CommunityKey=d543b8ba-9d3a-4121-85ce-5b68e6c31ce5&tab=digestviewer#bmff0c7f17-c36a-487e-9609-d5999cbd6d89) is interesting discussion and at the end ther is note " the TMF688 Event API is a generic API for eventing and streamaing platforms; that means that all Event API resource (Event,Topic,Hub) should be implemented by that streaming platform. (e.g. KAFKA)
The Topic resource is defined as container resource for Event resource .
and can be used to set-up a Topic/event graph for domain specific Event processing.
The Hub resource is defined  for holding subcription for other clients, which can be notified by s.c. CallBack Post operation of the Event API (see.API Notifications chapter) to other clients/Listeners
"

