import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ResourceOrder, ResourceOrderCreate, ResourceOrderUpdate} from "@adlatus-gui/domain/resource-order-management";

@Injectable({
    providedIn: 'root'
})
export class ResourceOrderService {

    constructor(public httpClient: HttpClient, @Inject('environment') private environment: Record<string, string | boolean | number>) {
    }

    createResourceOrder(resourceOrderCreate: ResourceOrderCreate) {
        return this.httpClient.post<ResourceOrder>((<string>this.environment.RESOURCE_ORDER_ENDPOINT), resourceOrderCreate);
    }

    retrieveAllResourceOrders() {
        return this.httpClient.get<ResourceOrder[]>((<string>this.environment.RESOURCE_ORDER_ENDPOINT));
    }

    retrieveResourceOrder(id: string) {
        return this.httpClient.get<ResourceOrder>(`${(<string>this.environment.RESOURCE_ORDER_ENDPOINT)}/${id}`);
    }

    patchResourceOrder(id: string, resourceOrderUpdate: ResourceOrderUpdate) {
        return this.httpClient.patch<ResourceOrder>(`${(<string>this.environment.RESOURCE_ORDER_ENDPOINT)}/${id}`, resourceOrderUpdate)
    }

    deleteResourceOrder(id: string) {
        return this.httpClient.delete<void>(`${(<string>this.environment.RESOURCE_ORDER_ENDPOINT)}/${id}`);
    }
}
