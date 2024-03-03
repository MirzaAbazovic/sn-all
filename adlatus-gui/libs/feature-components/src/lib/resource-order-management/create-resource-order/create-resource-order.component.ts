import {ChangeDetectionStrategy, Component, OnInit, ViewChild} from '@angular/core';
import {AsyncPipe, JsonPipe, NgForOf, NgIf} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {MatStepper, MatStepperModule} from "@angular/material/stepper";
import {MatInputModule} from "@angular/material/input";
import {EntryFormComponent} from "@adlatus-gui/feature-components/shared/components/entry-form";
import {ResourceSpecificationService} from "@adlatus-gui/business/resource-catalog-management";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ResourceSpecification} from "@adlatus-gui/domain/resource-catalog-management";
import {
    createResourceOrderFormConfig
} from "@adlatus-gui/domain/resource-order-management/configs/create-resource-order-form.config";
import {
    emptyResourceOrderCreate
} from "@adlatus-gui/domain/resource-order-management/constants/empty-resource-order-create";
import {ResourceOrder, ResourceOrderCreate, ResourceOrderItem} from "@adlatus-gui/domain/resource-order-management";
import {MatCardModule} from "@angular/material/card";
import {ResourceOrderService} from "@adlatus-gui/business/resource-order-management";
import {Router} from "@angular/router";

@Component({
    selector: 'adlatus-gui-create-resource-order',
    standalone: true,
    imports: [
        MatButtonModule,
        MatStepperModule,
        MatInputModule,
        NgIf,
        EntryFormComponent,
        NgForOf,
        AsyncPipe,
        MatCheckboxModule,
        JsonPipe,
        MatCardModule
    ],
    templateUrl: './create-resource-order.component.html',
    styleUrls: ['./create-resource-order.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateResourceOrderComponent implements OnInit {
    @ViewChild('stepper') stepper!: MatStepper;
    resourceSpecifications$ = this.resourceSpecificationService.getAllResourceSpecifications();
    selectedOption: ResourceSpecification | null = null;
    formConfig = createResourceOrderFormConfig;
    model: ResourceOrderCreate = {...emptyResourceOrderCreate};

    constructor(
        private resourceOrderService: ResourceOrderService,
        private resourceSpecificationService: ResourceSpecificationService,
        private router: Router) {
    }


    handleSelectResourceSpecification(resourceSpecification: ResourceSpecification) {
        this.selectedOption = resourceSpecification;

        if (resourceSpecification) {
            this.model.orderItem![0] = {
                resource: {
                    resourceCharacteristic: [],
                    id: '',
                    href: ''
                },
                resourceSpecification: {
                    id: resourceSpecification.id!,
                    name: resourceSpecification.name,
                }
            };

            this.stepper.next();
        }
    }

    onSubmit(event: object) {
        console.log(event)
        this.stepper.next();
    }

    finalSubmit() {
        this.resourceOrderService.createResourceOrder(this.model).subscribe(({
            next: (resourceOrder: ResourceOrder) => this.router.navigate(['orders', resourceOrder.id]),
            error: (err) => console.log(err)
        }))
    }

    ngOnInit(): void {
        this.resourceSpecifications$.subscribe({
            next: (values) => console.log(values)
        })
    }
}
