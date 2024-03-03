import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateResourceSpecificationComponent } from './create-resource-specification.component';

describe('CreateResourceSpecificationComponent', () => {
  let component: CreateResourceSpecificationComponent;
  let fixture: ComponentFixture<CreateResourceSpecificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateResourceSpecificationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateResourceSpecificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
