import {FormlyFieldConfig} from "@ngx-formly/core";
import { Validators } from '@angular/forms';

export const createIndividualFormConfig: FormlyFieldConfig[] = [
  {
    key: 'givenName',
    type: 'input',
    props: {
      label: 'First name',
      placeholder: 'John',
    },
    validators:{
      validation: [Validators.required]
    }
  },
  {
    key: 'familyName',
    type: 'input',
    props: {
      label: 'Last name',
      placeholder: 'Doe',
    },
    validators:{
      validation: [Validators.required]
    }
  },
  {
    key: 'birthDate',
    type: 'datepicker',
    props: {
      label: 'Birth Date',
      placeholder: 'mm-dd-yyyy',
      format: 'mm-dd-yyyy',
    },
  },
];
