import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormSectionWrapperComponent } from './form-section-wrapper.component';

describe('FormSectionWrapperComponent', () => {
  let component: FormSectionWrapperComponent;
  let fixture: ComponentFixture<FormSectionWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormSectionWrapperComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormSectionWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
