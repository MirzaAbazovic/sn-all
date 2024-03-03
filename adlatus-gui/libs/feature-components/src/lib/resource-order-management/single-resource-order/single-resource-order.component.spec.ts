import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleResourceOrderComponent } from './single-resource-order.component';

describe('SingleResourceOrderComponent', () => {
  let component: SingleResourceOrderComponent;
  let fixture: ComponentFixture<SingleResourceOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleResourceOrderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleResourceOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
