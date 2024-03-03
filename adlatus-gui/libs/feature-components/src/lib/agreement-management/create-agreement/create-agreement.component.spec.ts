import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateAgreementComponent} from './create-agreement.component';
import {MockService} from 'ng-mocks';
import {AgreementService} from '@adlatus-gui/business/agreement-management';
import {of, Subject} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {Router} from '@angular/router';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AdlatusFormlyModule} from '@adlatus-gui/feature-components/adlatus-formly/adlatus-formly.module';
import {By} from '@angular/platform-browser';
import {Agreement} from '@adlatus-gui/domain/agreement-management';
import {prefillFormData} from '@adlatus-gui/feature-components/test-utils/form-utils';

describe('CreateAgreementComponent', () => {
  let component: CreateAgreementComponent;
  let fixture: ComponentFixture<CreateAgreementComponent>;
  const agreementService = MockService(AgreementService);
  let agreementSubject = new Subject<Agreement>();
  let listener = agreementSubject.asObservable()
  let router = MockService(Router);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateAgreementComponent, RouterTestingModule.withRoutes([
        {
          path: '', component: CreateAgreementComponent
        },
        {
          path: '**', redirectTo: ''
        }
      ]), BrowserAnimationsModule, AdlatusFormlyModule],
      providers: [{
        provide: AgreementService,
        useValue: agreementService
      },
        {
          provide: Router,
          useValue: router
        }
      ],
      teardown: { destroyAfterEach: false }
    }).compileComponents();
    fixture = TestBed.createComponent(CreateAgreementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create form with fields', () => {
    const form = fixture.debugElement.query(By.css('adlatus-gui-entry-form'));
    const formInputs = form.queryAll(By.css('.mdc-text-field__input'));
    const repeats = form.queryAll(By.css('adlatus-gui-repeat-field'));

    expect(formInputs.length).toEqual(8);
    expect(repeats.length).toEqual(3);
  });

  it('should create agreement on submit of form and redirect page', (done) => {
    const {spy, routerSpy} = provideSpies();

    let form = fixture.debugElement.query(By.css('adlatus-gui-entry-form'));
    prefillFormData(form, getCreateAggreement(), ['name', 'agreementType']);
    fixture.detectChanges();

    listener.subscribe(result => {
      let createAgreement = getCreateAggreement();
      expect(result.name).toEqual(createAgreement.name);
      expect(result.agreementType).toEqual(createAgreement.agreementType);
      expect(spy).toHaveBeenCalled()
      done();
    });

    let submit = form.query(By.css('button[type="submit"]')).nativeElement;
    submit.click();
    expect(routerSpy).toHaveBeenCalled();
  });

  function getCreateAggreement() {
    return {
      name: 'test',
      agreementType: 'test',
      agreementItem: [],
      engagedParty: []
    }
  }

  function provideSpies() {
    const spy = jest.spyOn(agreementService, 'createAgreement').mockImplementation((parameter) => {
      let result: Agreement = {...getCreateAggreement()};
      result.id = '1';
      agreementSubject.next(result);
      return of(result);
    });

    let routerSpy =
      jest
        .spyOn(router, 'navigate')
        .mockImplementation(() => of(true).toPromise());

    return {spy, routerSpy};
  }
});
