import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListAgreementsComponent } from './list-agreements.component';

describe('ListAgreementsComponent', () => {
  let component: ListAgreementsComponent;
  let fixture: ComponentFixture<ListAgreementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListAgreementsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListAgreementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
