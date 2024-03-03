import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleAgreementComponent } from './single-agreement.component';

describe('SingleAgreementComponent', () => {
  let component: SingleAgreementComponent;
  let fixture: ComponentFixture<SingleAgreementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleAgreementComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleAgreementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
