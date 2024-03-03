import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Input, OnInit,
  Output,
  QueryList,
  ViewChildren
} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {FilterData, FilterParams} from "@adlatus-gui/domain/reporting-system/models/filters";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BehaviorSubject} from "rxjs";
import {MatInputModule} from "@angular/material/input";

@Component({
  selector: 'adlatus-gui-entries-filter',
  standalone: true,
  imports: [
    MatButtonModule,
    NgForOf,
    AsyncPipe,
    MatInputModule,
    NgIf
  ],
  templateUrl: './entries-filter.component.html',
  styleUrls: ['./entries-filter.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EntriesFilterComponent {
  @Input() filters!: FilterParams[];
  @Output() filterApply = new EventEmitter<FilterData[]>();
  appliedFilters$: BehaviorSubject<FilterData[]> = new BehaviorSubject<FilterData[]>([]);

  @ViewChildren('filterInput') filterQueryList!: QueryList<ElementRef>;

  applyFilters(): void {
    const filterData: FilterData[] = [];
    this.filterQueryList.forEach(val => {
      if (val.nativeElement.value === '')
        return;
      filterData.push({
        name: val.nativeElement.name,
        value: val.nativeElement.value
      });
    });
    this.appliedFilters$.next(filterData);
    this.filterApply.emit(filterData);
  }

  clearFilters() {
    this.filterQueryList.forEach(val => {
      val.nativeElement.value = '';
    });
    this.appliedFilters$.next([]);
    this.filterApply.emit([]);
  }
}
