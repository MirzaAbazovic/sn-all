import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {ActivatedRoute, Router} from "@angular/router";
import {ResourceSpecificationService} from "@adlatus-gui/business/resource-catalog-management";
import {Observable} from "rxjs";
import {ResourceSpecification} from "@adlatus-gui/domain/resource-catalog-management";
import {MatIconModule} from "@angular/material/icon";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {DeleteEntryDialogComponent} from "@adlatus-gui/feature-components/shared/components/delete-entry-dialog";
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'adlatus-gui-single-resource-specification',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDialogModule, MatListModule],
  templateUrl: './single-resource-specification.component.html',
  styleUrls: ['./single-resource-specification.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SingleResourceSpecificationComponent {
  private resourceSpecificationId: string = this.route.snapshot.params.id;
  resourceSpecification$: Observable<ResourceSpecification> = this.resourceSpecificationService.getResourceSpecification(this.resourceSpecificationId);

  constructor(
    private resourceSpecificationService: ResourceSpecificationService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
  ) {
  }

  deleteResourceSpecification(id: string) {
    const deleteEntryDialogRef = this.dialog.open(DeleteEntryDialogComponent);

    deleteEntryDialogRef.afterClosed().subscribe((payload: boolean) => {
      if (!payload)
        return;

      this.resourceSpecificationService.deleteResourceSpecification(id).subscribe({
        next: () => {
          this.router.navigate(['resource-specifications']).then()
        },
        error: (e) => console.log(e)
      });
    });

  }

  editResourceSpecification(id: string) {
    this.router.navigate(['resource-specifications', id, 'edit']).then()
  }
}
