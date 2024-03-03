import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AsyncPipe, JsonPipe, NgIf} from '@angular/common';
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {ResourceOrder} from "@adlatus-gui/domain/resource-order-management";
import {ResourceOrderService} from "@adlatus-gui/business/resource-order-management";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";

@Component({
    selector: 'adlatus-gui-single-resource-order',
    standalone: true,
    imports: [NgIf, AsyncPipe, MatButtonModule, MatCardModule, MatIconModule, JsonPipe],
    templateUrl: './single-resource-order.component.html',
    styleUrls: ['./single-resource-order.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SingleResourceOrderComponent {
    resourceOrderId: string = this.route.snapshot.params.id;

    resourceOrder$: Observable<ResourceOrder> = this.resourceOrderService.retrieveResourceOrder(this.resourceOrderId)

    constructor(private route: ActivatedRoute, private resourceOrderService: ResourceOrderService) {
    }
}
