import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SingleOrganizationComponent} from './single-organization.component';

describe('OrganizationComponent', () => {
  let component: SingleOrganizationComponent;
  let fixture: ComponentFixture<SingleOrganizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleOrganizationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleOrganizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
