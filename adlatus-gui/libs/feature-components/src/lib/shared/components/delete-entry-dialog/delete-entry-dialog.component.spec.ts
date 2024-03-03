import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteEntryDialogComponent } from './delete-entry-dialog.component';

describe('DeleteEntryDialogComponent', () => {
  let component: DeleteEntryDialogComponent;
  let fixture: ComponentFixture<DeleteEntryDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteEntryDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteEntryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
