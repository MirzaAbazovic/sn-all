import {FormlyFieldConfig} from "@ngx-formly/core";
import {
  resourceCharacteristicFormGroupConfig
} from "@adlatus-gui/domain/resource-catalog-management/configs/resource-characteristic-form-group.config";
import {
  resourceCharacteristicValueFormGroupConfig
} from "@adlatus-gui/domain/resource-catalog-management/configs/resource-characteristic-value-form-group.config";

export const updateResourceSpecificationFormConfig: FormlyFieldConfig[] = [
  {
    fieldGroup: [
      {
        key: 'name',
        type: 'input',
        props: {
          label: 'Specification Name',
          placeholder: 'Enter Name',
        }
      },
    ]
  },
  {
    key: 'resourceSpecCharacteristic',
    type: 'repeat-field',
    wrappers: ['section'],
    props: {
      sectionTitle: 'Characteristics',
      removeButtonText: 'Remove Characteristic',
    },
    fieldArray: {
      fieldGroup: [
        {
          fieldGroup: [
            ...resourceCharacteristicFormGroupConfig,
            {
              key: 'resourceSpecCharacteristicValue',
              type: 'repeat-field',
              wrappers: ['section'],
              props: {
                sectionTitle: 'Characteristic Values',
                removeButtonText: 'Remove Characteristic Value',
              },
              fieldArray: {
                fieldGroup: [
                  ...resourceCharacteristicValueFormGroupConfig,
                ]
              }
            }
          ]
        },
      ]
    }
  }
]

