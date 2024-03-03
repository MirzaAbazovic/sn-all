import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleResourceSpecificationComponent } from './single-resource-specification.component';

describe('SingleResourceSpecificationComponent', () => {
  let component: SingleResourceSpecificationComponent;
  let fixture: ComponentFixture<SingleResourceSpecificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleResourceSpecificationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleResourceSpecificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
