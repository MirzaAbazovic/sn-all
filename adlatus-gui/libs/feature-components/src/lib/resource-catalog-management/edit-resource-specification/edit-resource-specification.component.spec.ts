import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditResourceSpecificationComponent } from './edit-resource-specification.component';

describe('EditResourceSpecificationComponent', () => {
  let component: EditResourceSpecificationComponent;
  let fixture: ComponentFixture<EditResourceSpecificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditResourceSpecificationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EditResourceSpecificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
