import {ResourceOrderCreate} from "@adlatus-gui/domain/resource-order-management";


export const emptyResourceOrderCreate: ResourceOrderCreate = {
    name: undefined,
    note: [],
    orderItem: [],
    requestedCompletionDate: undefined
}
