import {FormlyFieldConfig} from "@ngx-formly/core";

export const resourceCharacteristicValueFormGroupConfig: FormlyFieldConfig[] = [
  {
    key: 'value',
    type: 'input',
    props: {
      label: 'Value',
      placeholder: 'Enter Value',
    }
  },
  {
    key: 'valueType',
    type: 'input',
    props: {
      label: 'Value Type',
      placeholder: 'Enter Value Type',
    }
  },
  {
    key: 'unitOfMeasure',
    type: 'input',
    props: {
      label: 'Unit Of Measure',
      placeholder: 'Enter Unit Of Measure',
    }
  }
]
