import {By} from '@angular/platform-browser';

export function getValueForFormField(input, valueObject, fields) {
  let inputType = input.getAttribute('id');
  inputType = inputType.split('_');
  inputType = inputType[inputType.length - 2];

  if (fields.includes(inputType)) {
    return valueObject[inputType]
  }

  return undefined;
}
// todo: Think how to handle custom components and their fill
// provide methods for custom components if needed
export function prefillFormData(form, valueObject, fields) {
  const formInputs = form.queryAll(By.css('.mdc-text-field__input'));
  for (let input of formInputs) {
    const valueForFormField = getValueForFormField(input.nativeElement, valueObject, fields);
    if (valueForFormField !== undefined) {
      input.nativeElement.value = valueForFormField;
      input.nativeElement.dispatchEvent(new Event('input'));
      input.nativeElement.dispatchEvent(new Event('change'));
    }
  }
}