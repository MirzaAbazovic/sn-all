import {TestBed} from '@angular/core/testing';
import {AgreementService} from './agreement.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Agreement, AgreementCreate, AgreementUpdate} from "@adlatus-gui/domain/agreement-management";
import agreements from '../test-data/agreement/agreementsGet.json';
import agreementUpdate from '../test-data/agreement/agreementCreateUpdate.json';
import agreementCreate from '../test-data/agreement/agreementCreateUpdate.json'
import agreementById from '../test-data/agreement/agreement.byId.json'

describe('AgreementService', () => {
  const environment = {
    ENDPOINT: 'http://localhost/agreementManagement/v4/agreement'
  }
  let service: AgreementService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AgreementService,
        {provide: 'environment', useValue: environment}
      ]
    });
    service = TestBed.inject(AgreementService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should return an array of agreements', (done) => {
    const mockAgreementsRequest: Agreement[] = agreements as unknown as Agreement[];

    service.retrieveAllAgreements().subscribe((agreementsResponse: Agreement[]) => {
      expect(agreementsResponse).toBeDefined();
      expect(agreementsResponse.length).toBe(mockAgreementsRequest.length);
      expect(JSON.stringify(agreementsResponse)).toEqual(JSON.stringify(mockAgreementsRequest))
      expect(agreementsResponse).toEqual(mockAgreementsRequest);
      done();
    });

    const req = httpTestingController.expectOne(environment.ENDPOINT);
    expect(req.request.method).toBe('GET');
    req.flush(mockAgreementsRequest);
    httpTestingController.verify();
  });

  it('should create agreement', (done) => {
    const createAgreementRequest: AgreementCreate = agreementCreate as unknown as AgreementCreate;
    service.createAgreement(createAgreementRequest).subscribe((createdAgreementResponse) => {
      expect(JSON.stringify(createdAgreementResponse)).toEqual(JSON.stringify(createAgreementRequest))
      expect(createdAgreementResponse).toEqual(createAgreementRequest);
      done();
    });

    const req = httpTestingController.expectOne(environment.ENDPOINT);
    expect(req.request.method).toBe('POST');
    req.flush(createAgreementRequest);
    httpTestingController.verify();
  });

  it('should retrieve an agreement by ID', (done) => {
    const id = '1';
    const agreementIdRequest: Agreement = agreementById as unknown as Agreement

    service.retrieveAgreement(id).subscribe(retrievedAgreementResponse => {
      expect(JSON.stringify(retrievedAgreementResponse)).toEqual(JSON.stringify(agreementIdRequest));
      expect(retrievedAgreementResponse).toEqual(agreementIdRequest);
      done();
    })

    const req = httpTestingController.expectOne(`${environment.ENDPOINT}/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(agreementIdRequest);
  });

  it('should delete by id', (done) => {
    const id = '1';
    const mockAgreementId: Agreement =  agreementById as unknown as Agreement;

    service.deleteAgreement(id).subscribe(() => {
      expect(true).toBeTruthy();
      done();
    });

    const req = httpTestingController.expectOne(`${environment.ENDPOINT}/${id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should patch/update an agreement', (done) => {
    const id = '1';
    const updateAgreementRequest: AgreementUpdate = agreementUpdate as unknown as AgreementCreate;
      service.patchAgreement(id, updateAgreementRequest).subscribe(updatedAgreementResponse => {
        expect(updatedAgreementResponse).toBeDefined();
        expect(JSON.stringify(updatedAgreementResponse)).toEqual(JSON.stringify(updateAgreementRequest));
        expect(updatedAgreementResponse).toEqual(updateAgreementRequest);
        done();
      });

      const req = httpTestingController.expectOne(`${environment.ENDPOINT}/${id}`);
      expect(req.request.method).toBe('PATCH');
      req.flush(updateAgreementRequest)
    })

  afterEach(() => {
    httpTestingController.verify();
  });

});

