import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AsyncPipe, DatePipe, JsonPipe, NgFor, NgForOf, NgIf} from '@angular/common';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {AgreementService} from '@adlatus-gui/business/agreement-management';
import {Agreement} from '@adlatus-gui/domain/agreement-management';
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {DeleteEntryDialogComponent} from "@adlatus-gui/feature-components/shared/components/delete-entry-dialog";

@Component({
  selector: 'adlatus-gui-single-agreement',
  standalone: true,
  imports: [NgIf, MatCardModule, MatIconModule, MatButtonModule, MatDialogModule, AsyncPipe, JsonPipe, NgFor, NgForOf, RouterLink, DatePipe],
  templateUrl: './single-agreement.component.html',
  styleUrls: ['./single-agreement.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SingleAgreementComponent {
  private agreementId: string = this.route.snapshot.params.id;

  agreement$: Observable<Agreement> = this.agreementService.retrieveAgreement(this.agreementId)

  constructor(
    private agreementService: AgreementService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  deleteAgreement(agreementId: string) {
    const deleteEntryDialogRef = this.dialog.open(DeleteEntryDialogComponent);

    deleteEntryDialogRef.afterClosed().subscribe((payload: boolean) => {
      if (!payload)
        return;

      this.agreementService.deleteAgreement(agreementId).subscribe({
        next: (data: any) => {
          this.router.navigate(['agreements'])
        },
        error: (e) => console.log(e)
      })
    });
  }
}
