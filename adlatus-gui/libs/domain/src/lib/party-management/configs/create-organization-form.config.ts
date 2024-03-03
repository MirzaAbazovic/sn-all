import {FormlyFieldConfig} from '@ngx-formly/core';
import { Validators } from '@angular/forms';

export const createOrganizationFormConfig: FormlyFieldConfig[] = [
  {
    key: 'tradingName',
    type: 'input',
    props: {
      label: 'Trading Name',
      placeholder: 'Enter Trading Name',
    },
    validators:{
      validation: [Validators.required]
    }
  },
  {
    key: 'name',
    type: 'input',
    props: {
      label: 'Name',
      placeholder: 'Enter Name',
    },
  },
  {
    key: 'nameType',
    type: 'input',
    props: {
      label: 'Name Type',
      placeholder: 'Enter Name Type',
    },
  },
  {
    key: 'organizationType',
    type: 'input',
    props: {
      label: 'Organization Type',
      placeholder: 'Enter Organization Type',
    },
  },
  // This below is needed in edit organization form
  // {
  //   key: 'status',
  //   type: 'select',
  //   props: {
  //     label: 'Organization State',
  //     placeholder: 'Enter Organization State',
  //     options: [
  //       {label: 'Initialized', value: 'initialized'},
  //       {label: 'Validated', value: 'validated'},
  //       {label: 'Closed', value: 'closed'},
  //     ],
  //   },
  // },
  {
    key: 'isHeadOffice',
    type: 'checkbox',
    props: {
      label: 'Head Office',
    },
  },
  {
    key: 'isLegalEntity',
    type: 'checkbox',
    props: {
      label: 'Legal Entity',
    },
  },
  {
    key: 'contactMedium',
    type: 'repeat-field',
    wrappers: ['section'],
    props: {
      sectionTitle: 'Contact Medium',
      addText: 'Add Contact Medium',
    },
    fieldArray: {
      fieldGroup: [
        {
          key: 'mediumType',
          type: 'input',
          props: {
            label: 'Medium Type',
          },
        },
        {
          key: 'preferred',
          type: 'checkbox',
          props: {
            label: 'Preferred',
          }
        },
        {
          key: 'characteristic',
          wrappers: ['section'],
          props: {
            sectionTitle: 'Characteristic',
          },
          fieldGroup: [
            {
              key: 'city',
              type: 'input',
              props: {
                label: 'City',
                placeholder: 'Enter City',
              }
            },
            {
              key: 'contactType',
              type: 'input',
              props: {
                label: 'Contact Type',
                placeholder: 'Enter Contact Type',
              }
            },
            {
              key: 'country',
              type: 'input',
              props: {
                label: 'Country',
                placeholder: 'Enter Country',
              }
            },
            {
              key: 'emailAddress',
              type: 'input',
              props: {
                label: 'Email Address',
                placeholder: 'Enter Email Address',
              }
            },
            {
              key: 'faxNumber',
              type: 'input',
              props: {
                label: 'Fax Number',
                placeholder: 'Enter Fax Number',
              }
            },
            {
              key: 'phoneNumber',
              type: 'input',
              props: {
                label: 'Phone Number',
                placeholder: 'Enter Phone Number',
              }
            },
            {
              key: 'postalCode',
              type: 'input',
              props: {
                label: 'Postal Code',
                placeholder: 'Enter Postal Code',
              }
            },
            {
              key: 'socialNetworkId',
              type: 'input',
              props: {
                label: 'Social Network Id',
                placeholder: 'Enter Social Network Id',
              }
            },
            {
              key: 'stateOrProvince',
              type: 'input',
              props: {
                label: 'State Or Province',
                placeholder: 'Enter State Or Province',
              }
            },
            {
              key: 'street1',
              type: 'input',
              props: {
                label: 'Street 1',
                placeholder: 'Enter Street 1',
              }
            },
            {
              key: 'street2',
              type: 'input',
              props: {
                label: 'Street 2',
                placeholder: 'Enter Street 2',
              }
            }
          ]
        },
      ],
    }
  },
];
