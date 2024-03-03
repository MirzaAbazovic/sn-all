import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {IndividualService} from "@adlatus-gui/business/party-management/individual.service";
import {Individual, IndividualCreate, IndividualUpdate} from "@adlatus-gui/domain/party-management";

describe('IndividualService', () => {
  const environment = {
    INDIVIDUAL_ENDPOINT: 'http://localhost/tmf-api/party/v4/individual',
  };
  let service: IndividualService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {provide: 'environment', useValue: environment}
      ]
    });
    service = TestBed.inject(IndividualService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create party-management', (done) => {

    const partyRequest: IndividualCreate = {
      birthDate: new Date('1990-05-15'),
      familyName: 'Smith',
      givenName: 'John',
    }

    service.createIndividual(partyRequest).subscribe((createdPartyRequest) => {
      expect(createdPartyRequest).toBeDefined();
      expect(JSON.stringify(createdPartyRequest)).toEqual(JSON.stringify(partyRequest));
      expect(createdPartyRequest).toEqual(partyRequest);
      done();
    });

    const req = httpTestingController.expectOne(environment.INDIVIDUAL_ENDPOINT);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(partyRequest);
    req.flush(partyRequest);
    httpTestingController.verify();
  })

  it('should return an array of party-management-individual', (done) => {

    const allPartyRequest: Individual[] = [{
      id: "1",
      birthDate: new Date('1990-05-15'),
      familyName: 'Smith',
      givenName: 'John',
    },
      {
        id: "2",
        birthDate: new Date('1992-05-25'),
        familyName: 'Michaelson',
        givenName: 'Michael',
      }
    ];

    service.retrieveAllIndividuals().subscribe((allIndividualsResponse: Individual[]) => {
     expect(allIndividualsResponse).toBeDefined();
     expect(allIndividualsResponse.length).toBe(allPartyRequest.length);
     expect(JSON.stringify(allIndividualsResponse)).toEqual(JSON.stringify(allPartyRequest));
      expect(allIndividualsResponse).toEqual(allPartyRequest);
      done();
    });

    const req = httpTestingController.expectOne(environment.INDIVIDUAL_ENDPOINT);
    expect(req.request.method).toBe('GET');
    req.flush(allPartyRequest);
    httpTestingController.verify();
  })

  it('should  retrieve party-management-individual by id', function (done) {

    const id = "1";
    const partyIndividualIdRequest: Individual = {
      id: "1",
      birthDate: new Date('1990-05-15'),
      familyName: 'Smith',
      givenName: 'John',
    };

    service.retrieveIndividual(id).subscribe((retrievedPartyIdRequest: Individual) => {
      expect(retrievedPartyIdRequest).toBeDefined();
      expect(JSON.stringify(retrievedPartyIdRequest)).toEqual(JSON.stringify(partyIndividualIdRequest));
      expect(retrievedPartyIdRequest).toEqual(partyIndividualIdRequest)
      done();
    });

    const req = httpTestingController.expectOne(`${environment.INDIVIDUAL_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(partyIndividualIdRequest);
  });

  it('should patchIndividual/update an party-individual', (done) => {
    const id = "1";
    const updateIndividualRequest: IndividualUpdate = {
      birthDate: new Date('1990-05-15'),
      familyName: 'Smith',
      givenName: 'John',
    };

    service.patchIndividual(id, updateIndividualRequest).subscribe((updateIndividualResponse: IndividualUpdate) => {
      expect(updateIndividualResponse).toBeDefined();
      expect(JSON.stringify(updateIndividualResponse)).toEqual(JSON.stringify(updateIndividualRequest));
      done();
    });

    const req = httpTestingController.expectOne(`${environment.INDIVIDUAL_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('PATCH');
    req.flush(updateIndividualRequest)
  })

  it('should delete party-individuals', () => {
    const id = "1";
    const mockDeleteIndividual: Individual = {
      id: "1",
      birthDate: new Date('1990-05-15'),
      familyName: 'Smith',
      givenName: 'John',
    };

    service.deleteIndividual(id).subscribe(() => {
      expect(true).toBeTruthy()
    });

    const req = httpTestingController.expectOne(`${environment.INDIVIDUAL_ENDPOINT}/${id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  })

});
