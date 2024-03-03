import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListResourceSpecificationsComponent } from './list-resource-specifications.component';

describe('ListResourceSpecificationsComponent', () => {
  let component: ListResourceSpecificationsComponent;
  let fixture: ComponentFixture<ListResourceSpecificationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListResourceSpecificationsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListResourceSpecificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
