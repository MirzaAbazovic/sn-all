import {TestBed} from '@angular/core/testing';

import {ResourceSpecificationService} from './resource-specification.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {
  ResourceSpecification,
  ResourceSpecificationCreate
} from "@adlatus-gui/domain/resource-catalog-management";
import resourceCatalogId from '../test-data/resource-catalog-management/resource-catalog.byId.json'
import resourceCatalogUpdate from '../test-data/resource-catalog-management/resource-catalogCreateUpdate.json'
import resourceCatalogCreate from '../test-data/resource-catalog-management/resource-catalogCreateUpdate.json'
import resourcesAllCatalog from '../test-data/resource-catalog-management/resource-catalogGet.json'
import resourceDelete from '../test-data/resource-catalog-management/resource-catalog.byId.json'

describe('ResourceSpecificationService', () => {
  const environment = {
    RESOURCE_SPECIFICATION_ENDPOINT: 'http://localhost/tmf-api/resourceCatalog/v4/resourceSpecification',
  };

  let service: ResourceSpecificationService;
  let httpTestingController: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ResourceSpecificationService,
        {provide: 'environment', useValue: environment}
      ]
    });
    service = TestBed.inject(ResourceSpecificationService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a resource specification', (done) => {

    const createResourceRequest: ResourceSpecificationCreate = resourceCatalogCreate as unknown as ResourceSpecificationCreate;

    service.createResourceSpecification(createResourceRequest).subscribe((createdResource) => {
      expect(JSON.stringify(createdResource)).toEqual(JSON.stringify(createResourceRequest))
      expect(createdResource).toEqual(createResourceRequest);
      done();
    });

    const req = httpTestingController.expectOne(environment.RESOURCE_SPECIFICATION_ENDPOINT);
    expect(req.request.method).toBe('POST');
    req.flush(createResourceRequest);
    httpTestingController.verify();
  });

  it('should return an array of resource-catalog-management', (done) => {

    const getAllCatalogResponse: ResourceSpecification[] = resourcesAllCatalog as unknown as ResourceSpecification[];

    service.getAllResourceSpecifications().subscribe((resourcesResponse: ResourceSpecification[]) => {
      expect(resourcesResponse).toBeDefined();
      expect(resourcesResponse.length).toBe(getAllCatalogResponse.length)
      expect(JSON.stringify(resourcesResponse)).toEqual(JSON.stringify(getAllCatalogResponse))
      expect(resourcesResponse).toEqual(getAllCatalogResponse);
      done();
    });

    const req = httpTestingController.expectOne(environment.RESOURCE_SPECIFICATION_ENDPOINT);
    expect(req.request.method).toBe('GET');
    req.flush(getAllCatalogResponse);
    httpTestingController.verify();
  })

  it('should retrieve an resource-catalog-management by ID', (done) => {
    const id = "1";
    const mockResourceByIdRequest: ResourceSpecification = resourceCatalogId as unknown as ResourceSpecification;

    service.getResourceSpecification(id).subscribe((retrievedResourceIdResponse: ResourceSpecification) => {
      expect(JSON.stringify(retrievedResourceIdResponse)).toEqual(JSON.stringify(mockResourceByIdRequest));
      expect(retrievedResourceIdResponse).toEqual(mockResourceByIdRequest);
      done();
    });

    const req = httpTestingController.expectOne(`${environment.RESOURCE_SPECIFICATION_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResourceByIdRequest);
  })

  it('should delete by id', (done) => {
    const id = "1";
    const mockDataDeleteId = resourceDelete;

    service.deleteResourceSpecification(id).subscribe(() => {
      expect(true).toBeTruthy();
      done();
    })

    const req = httpTestingController.expectOne(`${environment.RESOURCE_SPECIFICATION_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  })

  it('should patch/update an resource-catalog-management', (done) => {
    const id = "1";
    const updateResourceRequest: ResourceSpecification = resourceCatalogUpdate as unknown as ResourceSpecification;

    service.updateResourceSpecification(id, updateResourceRequest).subscribe(updateResourceResponse => {
      expect(updateResourceResponse).toBeDefined();
      expect(JSON.stringify(updateResourceResponse)).toEqual(JSON.stringify(updateResourceRequest));
      expect(updateResourceResponse).toEqual(updateResourceRequest);
      done();
    })

    const req = httpTestingController.expectOne(`${environment.RESOURCE_SPECIFICATION_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('PATCH');
    req.flush(updateResourceRequest);
  })

  afterEach(() => {
    httpTestingController.verify();
  });

});
