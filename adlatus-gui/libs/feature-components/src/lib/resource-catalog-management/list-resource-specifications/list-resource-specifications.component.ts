import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ColumnData, EntriesTableComponent} from "@adlatus-gui/feature-components/shared/components/entries-table";
import {ResourceSpecificationService} from "@adlatus-gui/business/resource-catalog-management";
import {Observable} from "rxjs";
import {ResourceSpecification} from "@adlatus-gui/domain/resource-catalog-management";
import {Router} from "@angular/router";

@Component({
  selector: 'adlatus-gui-list-resource-specifications',
  standalone: true,
  imports: [CommonModule, EntriesTableComponent],
  templateUrl: './list-resource-specifications.component.html',
  styleUrls: ['./list-resource-specifications.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListResourceSpecificationsComponent {
  resourceSpecifications$: Observable<ResourceSpecification[]> = this.resourceSpecificationService.getAllResourceSpecifications();

  columns: ColumnData[] = [
    {key: 'name', label: 'Name'},
    {key: 'id', label: 'Index'},
  ];

  constructor(private resourceSpecificationService: ResourceSpecificationService, private router: Router) {
  }

  handleOpenResourceSpecification(event: any) {
    this.router.navigate(['resource-specifications', event.id]).then()
  }
}
