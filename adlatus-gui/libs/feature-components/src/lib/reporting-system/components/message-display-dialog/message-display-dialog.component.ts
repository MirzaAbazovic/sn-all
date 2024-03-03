import {AfterViewChecked, ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {DialogData} from "@adlatus-gui/feature-components/reporting-system/views/order-details";
import {MatButtonModule} from "@angular/material/button";
import xmlFormat from 'xml-formatter';

declare const PR: any;

@Component({
  selector: 'adlatus-gui-message-display-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
  ],
  templateUrl: './message-display-dialog.component.html',
  styleUrls: ['./message-display-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessageDisplayDialogComponent implements AfterViewChecked {

  public ngAfterViewChecked(): any {
    PR.prettyPrint();
  }

  constructor(
    public dialogRef: MatDialogRef<MessageDisplayDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  get formattedMessage(): any {
    return xmlFormat(this.data.message.trim());
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
