import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RepeatFieldComponent } from './repeat-field.component';

describe('RepeatFieldComponent', () => {
  let component: RepeatFieldComponent;
  let fixture: ComponentFixture<RepeatFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RepeatFieldComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RepeatFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
