import {Component} from '@angular/core';
import {Observable} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {Organization} from "@adlatus-gui/domain/party-management";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {
  DeleteEntryDialogComponent
} from "@adlatus-gui/feature-components/shared/components/delete-entry-dialog/delete-entry-dialog.component";
import {OrganizationService} from "@adlatus-gui/business/party-management";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {AsyncPipe, DatePipe, JsonPipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'adlatus-gui-single-organization',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIconModule, MatDialogModule, NgIf, AsyncPipe, JsonPipe, NgForOf, DatePipe],
  templateUrl: './single-organization.component.html',
  styleUrls: ['./single-organization.component.scss'],
})
export class SingleOrganizationComponent {
  private organizationId: string = this.route.snapshot.params.id;

  organization$: Observable<Organization> = this.organizationService.retrieveOrganization(this.organizationId)

  constructor(
    private organizationService: OrganizationService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  deleteOrganization(organizationId: string) {
    const deleteEntryDialogRef = this.dialog.open(DeleteEntryDialogComponent);

    deleteEntryDialogRef.afterClosed().subscribe((payload: boolean) => {
      if (!payload)
        return;

      this.organizationService.deleteOrganization(organizationId).subscribe({
        next: () => this.router.navigate(['organizations']),
        error: (e) => console.log(e)
      })
    });
  }
}
