import {FormlyFieldConfig} from "@ngx-formly/core";
import { Validators } from '@angular/forms';

export const createAgreementFormConfig: FormlyFieldConfig[] = [
  {
    key: 'agreementType',
    type: 'input',
    props: {
      label: 'Type',
    },
    validators:{
      validation: [Validators.required]
    }
  },
  {
    key: 'description',
    type: 'input',
    props: {
      label: 'Description',
    },
  },
  {
    key: 'documentNumber',
    type: 'input',
    props: {
      label: 'Document number',
      type: 'number',
      max: Number.MAX_SAFE_INTEGER,
      min: Number.MIN_SAFE_INTEGER,
    },
  },
  {
    key: 'name',
    type: 'input',
    props: {
      label: 'Name'
    },
    validators:{
      validation: [Validators.required]
    }
  },
  {
    key: 'initialDate',
    type: 'datepicker',
    props: {
      label: 'Initial date',
    },
    defaultValue: new Date()
  },
  {
    name: 'Agreement period',
    key: 'agreementPeriod',
    props: {
      sectionTitle: 'Agreement period',
    },
    fieldGroup: [
      {
        key: 'startDateTime',
        type: 'datepicker',
        props: {
          label: 'Start date time',
        },
      },
      {
        key: 'endDateTime',
        type: 'datepicker',
        props: {
          label: 'End date time',
        },
      },
    ],
  },
  {
    key: 'version',
    type: 'input',
    props: {
      label: 'Version'
    }
  },
  {
    key: 'engagedParty',
    type: 'repeat-field',
    wrappers: ['section'],
    props: {
      sectionTitle: 'Engaged party',
      addText: 'Add Engaged Party',
    },
    validators:{
      validation: [Validators.required]
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
          key: 'role',
          type: 'input',
          props: {
            label: 'Role',
          },
        },
      ]
    }
  },
  {
    key: 'agreementItem',
    type: 'repeat-field',
    wrappers: ['section'],
    props: {
      sectionTitle: 'Agreement Item',
      addText: 'Add agreement item',
    },
    validators:{
      validation: [Validators.required]
    },
    fieldArray: {
      fieldGroup: [
        {
          key: 'productOffering',
          type: 'repeat-field',
          wrappers: ['section'],
          props: {
            sectionTitle: 'Product offering',
            addText: 'Add product offering',
          },
          fieldArray: {
            fieldGroup: [
              {
                key: 'name',
                type: 'input',
                props: {
                  label: 'Product name',
                  required: true
                },
              },
            ]
          }
        },
        {
          key: 'termOrCondition',
          type: 'repeat-field',
          wrappers: ['section'],
          props: {
            sectionTitle: 'Term or condition',
            addText: 'Add term or condition',
          },
          fieldArray: {
            fieldGroup: [
              {
                key: 'description',
                type: 'input',
                props: {
                  label: 'Description',
                },
              },
              {
                name: 'Valid for',
                key: 'validFor',
                props: {
                  sectionTitle: 'Valid for',
                },
                fieldGroup: [
                  {
                    key: 'startDateTime',
                    type: 'datepicker',
                    props: {
                      label: 'Start date time',
                    },
                  },
                  {
                    key: 'endDateTime',
                    type: 'datepicker',
                    props: {
                      label: 'End date time',
                    },

                  },
                ],
              },
            ]
          }
        }
      ]
    }
  },
  {
    key: 'characteristic',
    type: 'repeat-field',
    wrappers: ['section'],
    props: {
      sectionTitle: 'Characteristic',
      addText: 'Add characteristic',
    },
    fieldArray: {
      fieldGroup: [
        {
          type: 'input',
          key: 'name',
          props: {
            label: 'Name',
          },
        },
        {
          type: 'input',
          key: 'valueType',
          props: {
            label: 'Value type',
          },
        },
        {
          type: 'input',
          key: 'value',
          props: {
            label: 'Value',
          },
        },
      ],
    },
  },
];

