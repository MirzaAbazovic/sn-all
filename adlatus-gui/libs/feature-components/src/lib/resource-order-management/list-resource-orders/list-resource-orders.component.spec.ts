import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListResourceOrdersComponent } from './list-resource-orders.component';

describe('ListResourceOrdersComponent', () => {
  let component: ListResourceOrdersComponent;
  let fixture: ComponentFixture<ListResourceOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListResourceOrdersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListResourceOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
