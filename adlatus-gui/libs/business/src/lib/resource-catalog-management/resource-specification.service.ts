import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {
  ResourceSpecification,
  ResourceSpecificationCreate,
  ResourceSpecificationUpdate
} from "@adlatus-gui/domain/resource-catalog-management";

@Injectable({
  providedIn: 'root'
})
export class ResourceSpecificationService {

  constructor(private http: HttpClient, @Inject('environment') private environment: Record<string, string | number | boolean>) {
  }

  public getAllResourceSpecifications(): Observable<ResourceSpecification[]> {
    console.log(<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT)
    return this.http.get<ResourceSpecification[]>(<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT);
  }

  public createResourceSpecification(resourceSpecification: ResourceSpecificationCreate): Observable<any> {
    return this.http.post<ResourceSpecification>(<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT,
      resourceSpecification,
    );
  }

  public getResourceSpecification(id: string): Observable<ResourceSpecification> {
    return this.http.get<ResourceSpecification>(`${<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT}/${id}`);
  }

  updateResourceSpecification(resourceSpecificationId: string, event: ResourceSpecificationUpdate): Observable<ResourceSpecification> {
    return this.http.patch<ResourceSpecification>(`${<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT}/${resourceSpecificationId}`,
      event,
    );
  }

  deleteResourceSpecification(resourceSpecificationId: string): Observable<any> {
    return this.http.delete(`${<string>this.environment.RESOURCE_SPECIFICATION_ENDPOINT}/${resourceSpecificationId}`);
  }
}
