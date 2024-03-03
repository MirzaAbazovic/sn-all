import {FormlyFieldConfig} from "@ngx-formly/core";
import {Validators} from '@angular/forms';


const orderItemFieldConfig: FormlyFieldConfig = {
  key: 'orderItem[0]',
  fieldGroup: [
    {
      key: 'resource',
      fieldGroup: [
        {
          key: 'resourceCharacteristic',
          type: 'repeat-field',
          wrappers: ['section'],
          props: {
            sectionTitle: 'Resource characteristics',
            addText: 'Add resource characteristic',
          },
          fieldArray: {
            fieldGroup: [
              {
                key: 'name',
                type: 'input',
                props: {
                  label: 'Name',
                },
              },
              {
                key: 'value',
                type: 'input',
                props: {
                  label: 'Value',
                },
              },
            ]
          }
        }
      ]
    }
  ]
}

export const createResourceOrderFormConfig: FormlyFieldConfig[] = [
  {
    key: 'name',
    type: 'input',
    props: {
      label: 'Order name'
    },
    validators: {
      validation: [Validators.required]
    }
  },
  {
    key: 'requestedCompletionDate',
    type: 'datepicker',
    props: {
      label: 'Requested delivery date'
    },
    validators: {
      validation: [Validators.required]
    }
  },
  orderItemFieldConfig
]
