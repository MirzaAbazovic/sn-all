import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {AsyncPipe, NgIf} from '@angular/common';
import {ColumnData, EntriesTableComponent} from "@adlatus-gui/feature-components/shared/components/entries-table";
import {MatCardModule} from "@angular/material/card";
import {Observable} from 'rxjs';
import {Router} from "@angular/router";
import {ReportingService} from '@adlatus-gui/business/reporting-system';
import {FilterData} from '@adlatus-gui/domain/reporting-system/models/filters';

@Component({
  selector: 'adlatus-gui-list-archived-orders',
  standalone: true,
  imports: [EntriesTableComponent, MatCardModule, NgIf, AsyncPipe],
  templateUrl: './list-archived-orders.component.html',
  styleUrls: ['./list-archived-orders.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListArchivedOrdersComponent implements OnInit {
  reports$: Observable<any> = this.reportingService.reports$;
  columns: ColumnData[] = [
    {key: 'id', label: 'ID'},
    {key: 'orderName', label: 'Name'},
    {key: 'orderNumber', label: 'Order Number'},
    {key: 'orderDate', label: 'Order Date', type: 'date'},
    {key: 'expectedCompletionDate', label: 'Expected Completion Date', type: 'date'},
    {key: 'lineID', label: 'Line ID'},
    {key: 'messageInterface', label: 'Message Interface'},
    {key: 'status', label: 'Status'},
  ];

  ngOnInit() {
    this.reportingService.loadArchivedReports();
  }

  onPageChange(event: { pageIndex: number, pageSize: number }) {
    this.reportingService.changePage(event.pageIndex, event.pageSize);
  }

  onFilterChange(event: FilterData[]) {
    this.reportingService.changeFilter(event);
  }

  openResourceOrder(event: any) {
    this.router.navigate(['orders', event.orderNumber]).then();
  }

  constructor(private router: Router, private reportingService: ReportingService) {
  }
}
