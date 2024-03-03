import {Component} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {
  DeleteEntryDialogComponent
} from "@adlatus-gui/feature-components/shared/components/delete-entry-dialog/delete-entry-dialog.component";
import {Individual} from "@adlatus-gui/domain/party-management";
import {IndividualService} from "@adlatus-gui/business/party-management";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {MatIconModule} from "@angular/material/icon";
import {map, Observable, switchMap} from "rxjs";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";

@Component({
  selector: 'adlatus-gui-single-individual',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, MatDialogModule, NgIf, AsyncPipe, MatCardModule],
  templateUrl: './single-individual.component.html',
  styleUrls: ['./single-individual.component.scss'],
})
export class SingleIndividualComponent {
  private individualId: string = this.route.snapshot.params.id;

  individual$: Observable<Individual> = this.individualService.retrieveIndividual(this.individualId)

  constructor(
    private individualService: IndividualService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  deleteIndividual(individualId: string) {
    const deleteEntryDialogRef = this.dialog.open(DeleteEntryDialogComponent);

    deleteEntryDialogRef.afterClosed().subscribe((payload: boolean) => {
      if (!payload)
        return;

      this.individualService.deleteIndividual(individualId).subscribe({
        next: (data: any) => {
          this.router.navigate(['individuals']).then()
        },
        error: (e) => console.log(e)
      })
    });
  }
}
