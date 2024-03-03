import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateResourceOrderComponent } from './create-resource-order.component';

describe('CreateResourceOrderComponent', () => {
  let component: CreateResourceOrderComponent;
  let fixture: ComponentFixture<CreateResourceOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateResourceOrderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateResourceOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
