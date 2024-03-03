import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListIndividualsComponent } from './list-individuals.component';

describe('AllIndividualsComponent', () => {
  let component: ListIndividualsComponent;
  let fixture: ComponentFixture<ListIndividualsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListIndividualsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListIndividualsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
