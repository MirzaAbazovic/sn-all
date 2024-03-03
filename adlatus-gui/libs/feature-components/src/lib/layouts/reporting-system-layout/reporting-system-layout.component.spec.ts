import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReportingSystemLayoutComponent} from './reporting-system-layout.component';

describe('ReportingSystemLayoutComponent', () => {
  let component: ReportingSystemLayoutComponent;
  let fixture: ComponentFixture<ReportingSystemLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportingSystemLayoutComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportingSystemLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
