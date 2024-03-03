import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SingleIndividualComponent} from './single-individual.component';

describe('IndividiualComponent', () => {
  let component: SingleIndividualComponent;
  let fixture: ComponentFixture<SingleIndividualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleIndividualComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleIndividualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
