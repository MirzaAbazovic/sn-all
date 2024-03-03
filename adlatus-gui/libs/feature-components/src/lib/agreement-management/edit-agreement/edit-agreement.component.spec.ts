import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditAgreementComponent } from './edit-agreement.component';

describe('EditAgreementComponent', () => {
  let component: EditAgreementComponent;
  let fixture: ComponentFixture<EditAgreementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditAgreementComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EditAgreementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
