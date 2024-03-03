import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EntryFormComponent} from "libs/feature-components/src/lib/shared/components/entry-form";
import {MatCardModule} from "@angular/material/card";
import {MatRadioModule} from "@angular/material/radio";
import {
    createResourceSpecificationFormConfig
} from "@adlatus-gui/domain/resource-catalog-management/configs/create-resource-specification-form.config";
import {ResourceSpecificationCreate} from "@adlatus-gui/domain/resource-catalog-management";
import {
    getEmptyResourceSpecificationCreate
} from "@adlatus-gui/domain/resource-catalog-management/constants/empty-resource-specification-create";
import {ResourceSpecificationService} from "@adlatus-gui/business/resource-catalog-management";
import {Router} from "@angular/router";

@Component({
    selector: 'adlatus-gui-create-resource-specification',
    standalone: true,
    imports: [CommonModule, EntryFormComponent, MatCardModule, MatRadioModule],
    templateUrl: './create-resource-specification.component.html',
    styleUrls: ['./create-resource-specification.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateResourceSpecificationComponent {
    formFields = createResourceSpecificationFormConfig;
    model: ResourceSpecificationCreate = getEmptyResourceSpecificationCreate();

    constructor(private resourceSpecificationService: ResourceSpecificationService, private router: Router) {
    }

    onSubmit(event: object) {
        const data = event as ResourceSpecificationCreate;
        this.resourceSpecificationService.createResourceSpecification(data).subscribe({
            next: (response: ResourceSpecificationCreate) => {
                console.log(response);
                this.model = getEmptyResourceSpecificationCreate();
                this.router.navigate(['/resource-specifications'])
            },
            error: (error: any) => {
                console.log(error);
            }
        });
    }
}
