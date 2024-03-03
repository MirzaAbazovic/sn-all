import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ResourceSpecification, ResourceSpecificationUpdate} from "@adlatus-gui/domain/resource-catalog-management";
import {ActivatedRoute, Router} from "@angular/router";
import {ResourceSpecificationService} from "@adlatus-gui/business/resource-catalog-management";
import {
    updateResourceSpecificationFormConfig
} from "@adlatus-gui/domain/resource-catalog-management/configs/edit-resource-specification-form.config";
import {EntryFormComponent} from "libs/feature-components/src/lib/shared/components/entry-form";
import {MatCardModule} from "@angular/material/card";

@Component({
    selector: 'adlatus-gui-edit-resource-specification',
    standalone: true,
    imports: [CommonModule, EntryFormComponent, MatCardModule],
    templateUrl: './edit-resource-specification.component.html',
    styleUrls: ['./edit-resource-specification.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditResourceSpecificationComponent implements OnInit {
    private resourceSpecificationId: string = this.route.snapshot.params.id;
    formFields = updateResourceSpecificationFormConfig;
    resourceSpecification!: ResourceSpecificationUpdate;

    constructor(
        private resourceSpecificationService: ResourceSpecificationService,
        private route: ActivatedRoute,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) {
        //
    }

    ngOnInit(): void {
        this.resourceSpecificationService.getResourceSpecification(this.resourceSpecificationId).subscribe({
            next: (resourceSpecification: ResourceSpecification) => {
                const resSpec = resourceSpecification as ResourceSpecificationUpdate;
                this.resourceSpecification = {...resSpec}
                // this.cdr.detectChanges();
                this.cdr.markForCheck()
            },
            error: (e) => console.log(e)
        });
    }

    onSubmit(event: object) {
        const resourceSpecification: ResourceSpecification = event as ResourceSpecificationUpdate;
        this.resourceSpecificationService.updateResourceSpecification(this.resourceSpecificationId, resourceSpecification).subscribe({
            next: () => {
                this.router.navigate(['resource-specifications']).then()
            },
            error: (e) => console.log(e)
        });
    }
}
