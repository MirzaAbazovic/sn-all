import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ListActiveOrdersComponent} from './list-active-orders.component';

describe('ListActiveOrdersComponent', () => {
  let component: ListActiveOrdersComponent;
  let fixture: ComponentFixture<ListActiveOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListActiveOrdersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListActiveOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
