import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ResourceOrderService} from '@adlatus-gui/business/resource-order-management';
import {Observable} from 'rxjs';
import {MatCardModule} from '@angular/material/card';
import {Router} from '@angular/router';
import {ResourceOrder} from "@adlatus-gui/domain/resource-order-management";
import {ColumnData, EntriesTableComponent} from '@adlatus-gui/feature-components/shared/components/entries-table';


@Component({
    selector: 'adlatus-gui-list-resource-orders',
    standalone: true,
    imports: [CommonModule, EntriesTableComponent, MatCardModule],
    templateUrl: './list-resource-orders.component.html',
    styleUrls: ['./list-resource-orders.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListResourceOrdersComponent {
    resourceOrders$: Observable<ResourceOrder[]> = this.resourceOrderService.retrieveAllResourceOrders();

    columns: ColumnData[] = [
        {key: 'name', label: 'Name'},
        {key: 'requestedCompletionDate', label: 'requestedCompletionDate'},
    ];

    constructor(private resourceOrderService: ResourceOrderService, private router: Router) {
    }

    openResourceOrder(event: any) {
        this.router.navigate(['orders', (<ResourceOrder>event).id!]);
    }
}
