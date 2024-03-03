import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {OrderDetails} from "@adlatus-gui/domain/reporting-system";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {AsyncPipe, JsonPipe, NgClass, NgForOf, NgIf, NgStyle} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {ReportingService} from "@adlatus-gui/business/reporting-system";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {
  MessageDisplayDialogComponent
} from "@adlatus-gui/feature-components/reporting-system/components/message-display-dialog/message-display-dialog.component";

export interface DialogData {
  message: string;
}

@Component({
  selector: 'adlatus-gui-order-details',
  standalone: true,
  imports: [
    MatCardModule,
    MatInputModule,
    FormsModule,
    NgStyle,
    MatIconModule,
    NgForOf,
    NgIf,
    AsyncPipe,
    JsonPipe,
    NgClass,
    MatDialogModule,
  ],
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrderDetailsComponent {
  orderDetails$!: Observable<OrderDetails>;

  constructor(private route: ActivatedRoute, private reportingService: ReportingService, public dialog: MatDialog) {
    const orderNumber = this.route.snapshot.params['orderNumber'];
    this.orderDetails$ = this.reportingService.getOrderDetails(orderNumber);
  }

  getFormattedOrderStatus(status: string): string {
    switch (status) {
      case 'inProgress':
        return 'In Progress';
      case 'rejected':
        return 'Rejected';
      case 'completed':
        return 'Completed';
      default:
        return 'In Progress';
    }
  }

  openDialog(message: string): void {
    this.dialog.open(MessageDisplayDialogComponent, {
      data: { message: message },
    });
  }
}
