import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ListArchivedOrdersComponent} from './list-archived-orders.component';

describe('ListArchivedOrdersComponent', () => {
  let component: ListArchivedOrdersComponent;
  let fixture: ComponentFixture<ListArchivedOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListArchivedOrdersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListArchivedOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
