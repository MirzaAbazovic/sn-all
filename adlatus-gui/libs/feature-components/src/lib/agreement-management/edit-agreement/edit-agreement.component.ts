import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AsyncPipe, NgIf} from '@angular/common';
import {Observable} from "rxjs";
import {Agreement, AgreementUpdate, editAgreementFormConfig} from "@adlatus-gui/domain/agreement-management";
import {AgreementService} from "@adlatus-gui/business/agreement-management";
import {ActivatedRoute, Router} from "@angular/router";
import {MatCardModule} from "@angular/material/card";
import {EntryFormComponent} from "@adlatus-gui/feature-components/shared/components/entry-form";
import {FormlyFieldConfig} from "@ngx-formly/core";

@Component({
  selector: 'adlatus-gui-edit-agreement',
  standalone: true,
  imports: [NgIf, MatCardModule, AsyncPipe, EntryFormComponent],
  templateUrl: './edit-agreement.component.html',
  styleUrls: ['./edit-agreement.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditAgreementComponent {
  editAgreementFormConfig: FormlyFieldConfig[] = editAgreementFormConfig;

  private agreementId: string = this.route.snapshot.params.id;

  agreement$: Observable<Agreement> = this.agreementService.retrieveAgreement(this.agreementId);

  constructor(private agreementService: AgreementService, private route: ActivatedRoute, private router: Router) {
  }

  onSubmit(event: object) {
    this.agreementService.patchAgreement(this.agreementId, (<AgreementUpdate>event)).subscribe({
      next: (updatedAgreement: Agreement) => {
        this.router.navigate(['agreements', updatedAgreement.id])
      },
      error: (err) => console.log(err)
    })
  }
}
