import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MessageDisplayDialogComponent} from './message-display-dialog.component';

describe('MessageDisplayDialogComponent', () => {
  let component: MessageDisplayDialogComponent;
  let fixture: ComponentFixture<MessageDisplayDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageDisplayDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageDisplayDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
