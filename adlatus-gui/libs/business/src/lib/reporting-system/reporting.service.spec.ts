import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReportingService } from './reporting.service';

describe('ReportingService', () => {
  let service: ReportingService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReportingService, { provide: 'environment', useValue: { ACTIVE_REPORTS_ENDPOINT: '/active', ARCHIVED_REPORTS_ENDPOINT: '/archived' } }]
    });

    service = TestBed.inject(ReportingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  xit('should fetch active reports', () => {
    const mockReports = [{ orderName: 'Test Order' }];
    const params = '?status=active';

    // service.loadActiveReports(params).subscribe(reports => {
    //   expect(reports).toEqual(mockReports);
    // });

    const req = httpMock.expectOne('/active' + params);
    expect(req.request.method).toBe('GET');
    req.flush(mockReports);
  });

  xit('should fetch archived reports', () => {
    const mockReports = [{ orderName: 'Test Order' }];
    const params = '?status=archived';

    // service.loadArchivedReports(params).subscribe(reports => {
    //   expect(reports).toEqual(mockReports);
    // });

    const req = httpMock.expectOne('/archived' + params);
    expect(req.request.method).toBe('GET');
    req.flush(mockReports);
  });
});
