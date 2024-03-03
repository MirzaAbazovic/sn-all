import { TestBed } from '@angular/core/testing';
import resourceCreate from '../test-data/resource-order-management/resource-orderCreate-Update.json'
import resourceUpdate from '../test-data/resource-order-management/resource-orderCreate-Update.json'
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {ResourceOrder, ResourceOrderCreate, ResourceOrderUpdate} from "@adlatus-gui/domain/resource-order-management";
import {ResourceOrderService} from "@adlatus-gui/business/resource-order-management/resource-order.service";
import resourceById from '../test-data/resource-order-management/resource-orderById.json';
import resourceGetAll from '../test-data/resource-order-management/resource-catalogGet.json';

describe('ResourceOrderService', () => {
  const environment = {
    RESOURCE_ORDER_ENDPOINT: 'http://localhost/tmf-api/resourceOrdering/v4/resourceOrder',
  };

  let service: ResourceOrderService;
  let httpTestingController: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule],
      providers:[ResourceOrderService,
        {provide: 'environment', useValue: environment}
      ]
    });
    service = TestBed.inject(ResourceOrderService);
    httpTestingController = TestBed.inject(HttpTestingController)
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create resource-order-management', function (done) {
       const createROMRequest: ResourceOrderCreate = resourceCreate as unknown as ResourceOrderCreate;

       service.createResourceOrder(createROMRequest).subscribe((createROMResponse: ResourceOrderCreate) => {
         expect(JSON.stringify(createROMResponse)).toEqual(JSON.stringify(createROMRequest));
         expect(createROMResponse).toEqual(createROMRequest);
         done();
       });
       const request = httpTestingController.expectOne(environment.RESOURCE_ORDER_ENDPOINT);
       expect(request.request.method).toBe('POST');
       request.flush(createROMRequest);
       httpTestingController.verify();
  });

  it('should patch/update an resource-order-management', function (done) {
    const id = "1";
    const updateROMRequest:ResourceOrderUpdate = resourceUpdate as unknown as ResourceOrderUpdate;

    service.patchResourceOrder(id, updateROMRequest).subscribe((updateROMResponse: ResourceOrderUpdate) =>{
      expect(updateROMResponse).toBeDefined();
      expect(JSON.stringify(updateROMResponse)).toEqual(JSON.stringify(updateROMRequest));
      expect(updateROMResponse).toEqual(updateROMRequest);
      done();
    })
    const request = httpTestingController.expectOne(`${environment.RESOURCE_ORDER_ENDPOINT}/${id}`);
    expect(request.request.method).toBe('PATCH');
    request.flush(updateROMRequest);
  });

  it('should delete by id ', function (done) {
    const id = "1";

    service.deleteResourceOrder(id).subscribe(() => {
      expect(true).toBeTruthy();
      done();
    });
    const request = httpTestingController.expectOne(`${environment.RESOURCE_ORDER_ENDPOINT}/${id}`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });

  it('should retrieve an resource-order-management by ID', function (done) {
    const id = "1";
    const updateRomRequest: ResourceOrder = resourceById as unknown as ResourceOrder;

    service.retrieveResourceOrder(id).subscribe((retrievedResourceResponse: ResourceOrder) => {
      expect(JSON.stringify(retrievedResourceResponse)).toEqual(JSON.stringify(updateRomRequest));
      expect(retrievedResourceResponse).toEqual(updateRomRequest);
      done();
    });
    const request = httpTestingController.expectOne(`${environment.RESOURCE_ORDER_ENDPOINT}/${id}`);
    expect(request.request.method).toBe('GET');
    request.flush(updateRomRequest);
  });

  it('should return an array of resource-order-management', (done) => {
    const allROMRequest: ResourceOrder[] = resourceGetAll as unknown as ResourceOrder[];

    service.retrieveAllResourceOrders().subscribe((allROMResponse: ResourceOrder[]) =>{
      expect(allROMResponse).toBeDefined();
      expect(allROMResponse.length).toBe(allROMRequest.length)
      expect(JSON.stringify(allROMResponse)).toEqual(JSON.stringify(allROMRequest))
      expect(allROMResponse).toEqual(allROMRequest);
      done();
    });

    const request = httpTestingController.expectOne(environment.RESOURCE_ORDER_ENDPOINT);
    expect(request.request.method).toBe('GET');
    request.flush(allROMRequest);
    httpTestingController.verify();


    })
});
