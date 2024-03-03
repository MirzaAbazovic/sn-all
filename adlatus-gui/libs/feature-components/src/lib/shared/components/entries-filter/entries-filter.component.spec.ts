import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EntriesFilterComponent } from './entries-filter.component';

describe('EntriesFilterComponent', () => {
  let component: EntriesFilterComponent;
  let fixture: ComponentFixture<EntriesFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntriesFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EntriesFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
