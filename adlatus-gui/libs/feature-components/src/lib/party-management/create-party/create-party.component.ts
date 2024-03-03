import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatCardModule} from "@angular/material/card";
import {MatTabsModule} from "@angular/material/tabs";
import {FormlyFieldConfig} from "@ngx-formly/core";
import {MatRadioModule} from "@angular/material/radio";
import {FormsModule} from "@angular/forms";
import {
  getEmptyIndividualCreate,
  getEmptyOrganizationCreate,
  IndividualCreate,
  OrganizationCreate,
  createIndividualFormConfig,
  createOrganizationFormConfig,
} from "@adlatus-gui/domain/party-management";
import {EntryFormComponent} from "libs/feature-components/src/lib/shared/components/entry-form";
import {IndividualService, OrganizationService} from "@adlatus-gui/business/party-management";

type CreatePartyOptions = {
  formConfig: FormlyFieldConfig[];
  model: IndividualCreate | OrganizationCreate;
}

@Component({
  selector: 'adlatus-gui-new-party',
  standalone: true,
  imports: [MatCardModule, MatTabsModule, EntryFormComponent, MatRadioModule, FormsModule],
  templateUrl: './create-party.component.html',
  styleUrls: ['./create-party.component.scss'],
})
export class CreatePartyComponent {
  options: Record<'individual' | 'organization', CreatePartyOptions> = {
    'individual': {
      formConfig: createIndividualFormConfig,
      model: getEmptyIndividualCreate()
    },

    'organization': {
      formConfig: createOrganizationFormConfig,
      model: getEmptyOrganizationCreate()
    }
  }

  selectedOption: (keyof typeof this.options) = 'individual';

  constructor(private individualService: IndividualService, private organizationService: OrganizationService, private router: Router) {
  }

  handleCreateOrganization(organizationCreate: OrganizationCreate) {
    this.organizationService.createOrganization(organizationCreate).subscribe({
      next: (organization) => this.router.navigate(['organizations', organization.id]),
      error: (err) => {
        // TODO: Toastr
        console.log(err)
      }
    })
  }

  handleIndividualCreate(individualCreate: IndividualCreate) {
    this.individualService.createIndividual(individualCreate).subscribe({
      next: (individual) => this.router.navigate(['individuals', individual.id]),
      error: (err) => {
        // TODO: Toastr
        console.log(err)
      }
    })
  }

  onSubmit(event: object) {
    switch (this.selectedOption) {
      case 'individual':
        this.handleIndividualCreate((<IndividualCreate>event));
        break;

      case 'organization':
        this.handleCreateOrganization((<OrganizationCreate>event));
        break;
    }
  }
}
