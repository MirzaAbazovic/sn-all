import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component, ElementRef, EventEmitter,
  Input,
  OnChanges,
  OnInit, Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatSort, MatSortModule, Sort} from "@angular/material/sort";
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import { NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {
  EntriesFilterComponent
} from "@adlatus-gui/feature-components/shared/components/entries-filter/entries-filter.component";
import {FilterData, FilterParams} from "@adlatus-gui/domain/reporting-system/models/filters";

export type ColumnData = {
  key: string;
  label: string;
  type?: string;
}

@Component({
  selector: 'adlatus-gui-entries-table',
  standalone: true,
  imports: [
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    NgForOf,
    NgIf,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    FormsModule,
    MatIconModule,
    EntriesFilterComponent,
    NgSwitchCase,
    NgSwitch,
    NgSwitchDefault
  ],
  templateUrl: './entries-table.component.html',
  styleUrls: ['./entries-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntriesTableComponent implements OnInit, OnChanges {
  @Input({required: true}) data!: any;
  @Output() clickHandler: EventEmitter<any> = new EventEmitter<any>();
  @Input({required: false}) pageSize: number = 10;
  @Input({required: false}) pageIndex: number = 0;
  @Input({required: false}) totalPages: number = 0;
  @Input({required: false}) paginationOptions: any[] = [
    {value: 5, viewValue: '5'},
    {value: 10, viewValue: '10'},
    {value: 20, viewValue: '20'},
  ]
  @Input({required: true}) columns: ColumnData[] = [];
  @Output() pageChange: EventEmitter<{ pageIndex: number, pageSize: number }> = new EventEmitter();
  @Output() filterChange: EventEmitter<FilterData[]> = new EventEmitter();


  filterParams: FilterParams[] = [
    {
      displayName: 'Order Id',
      name: 'orderId',
      type: 'text'
    },
    {
      displayName: 'From Order Date',
      name: 'fromOrderDate',
      type: 'date'
    },
    {
      displayName: 'To Order Date',
      name: 'toOrderDate',
      type: 'date'
    },
    {
      displayName: 'Line Id',
      name: 'lineId',
      type: 'text'
    }
  ]
  dataSource: MatTableDataSource<typeof this.data> = new MatTableDataSource();


  @ViewChild(MatSort)
  sort: MatSort = new MatSort();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild('nextButton') nextButton!: ElementRef;
  @ViewChild('previousButton') previousButton!: ElementRef;

  constructor(private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.columns = [
      ...this.columns,
      {
        key: 'action',
        label: 'Action'
      }
    ]
    this.dataSource.data = this.data.map((entity: any) => {
      return {
        ...entity,
        action: 'View'
      }
    });

    this.dataSource.sort = this.sort;
  }

  handleSortChange(event: Sort) {
    console.log(event);
  }

  get displayColumns() {
    return this.columns.map(column => column.key)
  }

  onPageSizeChange(event: any) {
    this.pageSize = Number(event.target.value);
    this.pageIndex = 0;
    this.pageChange.emit({pageIndex: this.pageIndex, pageSize: this.pageSize});
  }

  goToPreviousPage() {
    if (this.pageIndex > 0) {
      this.pageIndex--;
      this.pageChange.emit({pageIndex: this.pageIndex, pageSize: this.pageSize});
    }
  }

  goToNextPage() {
    if (this.pageIndex < this.totalPages - 1) {
      this.pageIndex++;
      this.pageChange.emit({pageIndex: this.pageIndex, pageSize: this.pageSize});
    }
  }

  handleFilterApply(event: FilterData[]) {
    this.filterChange.emit(event);
  }

  ngOnChanges(simple: SimpleChanges) {
    if (simple.data)
      this.dataSource.data = simple.data.currentValue;
    if (simple.sort)
      this.dataSource.sort = simple.sort.currentValue;
    this.changeDetectorRef.detectChanges();
  }

  detailsClick(event: any) {
    console.log("Details click", event);
    this.clickHandler.emit(event);
  }

  getCellValue(entity: { [x: string]: any; }, column: ColumnData) {
    if (column.type) {
      switch (column.type) {
        case 'date':
          return entity[column.key] ? new Date(entity[column.key]).toLocaleDateString() : null;
        default:
          return entity[column.key];
      }
    } else return entity[column.key];
  }
}
