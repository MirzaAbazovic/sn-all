import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
    Organization,
    OrganizationCreate,
    OrganizationUpdate
} from "@adlatus-gui/domain/party-management";

@Injectable({
    providedIn: 'root'
})
export class OrganizationService {

    constructor(private httpClient: HttpClient, @Inject('environment') private environment: Record<string, string | boolean | number>) {
    }

    createOrganization(organizationCreate: OrganizationCreate) {
        return this.httpClient.post<Organization>((<string>this.environment.ORGANIZATION_ENDPOINT), organizationCreate);
    }

    retrieveAllOrganizations() {
        console.log('retrieveAllOrganizations')
        return this.httpClient.get<Organization[]>((<string>this.environment.ORGANIZATION_ENDPOINT));
    }

    retrieveOrganization(id: string) {
        return this.httpClient.get<Organization>(`${(<string>this.environment.ORGANIZATION_ENDPOINT)}/${id}`);
    }

    patchOrganization(id: string, organizationUpdate: OrganizationUpdate) {
        return this.httpClient.patch<Organization>(`${(<string>this.environment.ORGANIZATION_ENDPOINT)}/${id}`, organizationUpdate)
    }

    deleteOrganization(id: string) {
        return this.httpClient.delete<void>(`${(<string>this.environment.ORGANIZATION_ENDPOINT)}/${id}`);
    }
}
