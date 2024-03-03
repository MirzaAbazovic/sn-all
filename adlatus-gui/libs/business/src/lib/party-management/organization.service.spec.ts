import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {OrganizationService} from "@adlatus-gui/business/party-management/organization.service";
import {Organization, OrganizationCreate, OrganizationUpdate} from "@adlatus-gui/domain/party-management";
import  organizationCreateUpdate from '../test-data/party-management/organizations/partyOrganizationsCreateUpdate.json'
import  organizationGet from '../test-data/party-management/organizations/partyOrganizationsGet.json';
import  organizationById from '../test-data/party-management/organizations/partyOrganizationById.json';

describe('OrganizationService', () => {
  const environment = {
    ORGANIZATION_ENDPOINT: 'http://localhost/tmf-api/party/v4/organization'
  };
  let service: OrganizationService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {provide: 'environment', useValue: environment}
      ]
    });
    service = TestBed.inject(OrganizationService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create party-management-organization', (done) => {
    const createOrganizationRequest: OrganizationCreate = organizationCreateUpdate as unknown as OrganizationCreate;

    service.createOrganization(createOrganizationRequest).subscribe((createOrganizationResponse) => {
          expect(JSON.stringify(createOrganizationResponse)).toEqual(JSON.stringify(createOrganizationRequest));
          expect(createOrganizationResponse).toEqual(createOrganizationRequest);
          done();
    });

    const req = httpTestingController.expectOne(environment.ORGANIZATION_ENDPOINT);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createOrganizationRequest);
    req.flush(createOrganizationRequest);
    httpTestingController.verify();
  })

  it('should return an array of party-management-organization', (done) => {
    const organizationAllRequest: Organization[] = organizationGet as unknown as Organization[];

    service.retrieveAllOrganizations().subscribe((allOrganizationResponse: Organization[]) => {
      expect(allOrganizationResponse).toBeDefined();
      expect(allOrganizationResponse.length).toBe(organizationAllRequest.length);
      expect(JSON.stringify(allOrganizationResponse)).toEqual(JSON.stringify(organizationAllRequest));
      expect(allOrganizationResponse).toEqual(organizationAllRequest);
      done();
    })

    const req = httpTestingController.expectOne(environment.ORGANIZATION_ENDPOINT);
    expect(req.request.method).toBe('GET');
    req.flush(organizationAllRequest);
    httpTestingController.verify();
  })

  it('should retrieve party-management-organization-individual by id', function (done) {
    const id = "1";
    const mockOrganizationIdRequest: Organization = organizationById as unknown as Organization;

    service.retrieveOrganization(id).subscribe((retrievedOrganizationIdResponse: Organization) => {
      expect(JSON.stringify(retrievedOrganizationIdResponse)).toEqual(JSON.stringify(mockOrganizationIdRequest));
      expect(retrievedOrganizationIdResponse).toEqual(mockOrganizationIdRequest);
      done();
    })
    const req = httpTestingController.expectOne(`${environment.ORGANIZATION_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrganizationIdRequest);
  });

  it('should patchIndividual/update an party-organization', function (done) {
    const id = "1";
    const updateOrganizationRequest: OrganizationUpdate = organizationCreateUpdate as unknown as OrganizationUpdate;

    service.patchOrganization(id, updateOrganizationRequest).subscribe((updateOrganizationResponse) => {
      expect(updateOrganizationResponse).toBeDefined();
      expect(JSON.stringify(updateOrganizationResponse)).toEqual(JSON.stringify(updateOrganizationRequest));
      expect(updateOrganizationResponse).toEqual(updateOrganizationRequest);
      done();
    });

    const req = httpTestingController.expectOne(`${environment.ORGANIZATION_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('PATCH');
    req.flush(updateOrganizationRequest);
  });

  it('should delete organization', (done) => {
    const id = "1";
    const updateOrganizationResponse: Organization = organizationById as unknown as Organization;

    service.deleteOrganization(id).subscribe(() => {
      expect(true).toBeTruthy();
      done();
    })
    const req = httpTestingController.expectOne(`${environment.ORGANIZATION_ENDPOINT}/${id}`)
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

})

