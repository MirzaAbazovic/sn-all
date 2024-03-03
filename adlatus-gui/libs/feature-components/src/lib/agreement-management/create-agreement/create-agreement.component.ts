import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatRadioModule} from '@angular/material/radio';
import {FormlyFieldConfig} from '@ngx-formly/core';
import {EntryFormComponent} from 'libs/feature-components/src/lib/shared/components/entry-form';
import {AgreementService} from '@adlatus-gui/business/agreement-management';
import {
  AgreementCreate,
  createAgreementFormConfig,
  emptyAgreementCreate
} from '@adlatus-gui/domain/agreement-management';


@Component({
  selector: 'adlatus-gui-create-agreement',
  standalone: true,
  imports: [MatCardModule, EntryFormComponent, MatCardModule, MatRadioModule],
  templateUrl: './create-agreement.component.html',
  styleUrls: ['./create-agreement.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAgreementComponent {
  formConfig: FormlyFieldConfig[] = createAgreementFormConfig;
  model: AgreementCreate = {...emptyAgreementCreate};

  constructor(private agreementService: AgreementService, private router: Router) {
  }

  onSubmit(event: object) {
    this.agreementService.createAgreement((<AgreementCreate>event)).subscribe({
      next: (agreement) => this.router.navigate(['agreements', agreement.id]),
      error: (err) => {
        // TODO: ngx-toastr
        console.log(err)
      }
    })
  }
}
