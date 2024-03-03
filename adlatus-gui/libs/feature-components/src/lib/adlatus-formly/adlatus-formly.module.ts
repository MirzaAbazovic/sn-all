import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {FormlyModule} from "@ngx-formly/core";
import {FormlyMaterialModule} from "@ngx-formly/material";
import {AutocompleteFieldComponent} from "libs/feature-components/src/lib/adlatus-formly/components/autocomplete-field";
import {RepeatFieldComponent} from "libs/feature-components/src/lib/adlatus-formly/components/repeat-field";
import {MatNativeDateModule} from "@angular/material/core";
import {FormlyMatDatepickerModule} from "@ngx-formly/material/datepicker";
import {
  FormSectionWrapperComponent
} from "@adlatus-gui/feature-components/adlatus-formly/components/form-section-wrapper";

@NgModule({
  declarations: [],
  imports: [
    ReactiveFormsModule,
    FormlyModule.forRoot({
      wrappers: [
        {name: 'section', component: FormSectionWrapperComponent},
      ],
      types: [
        {name: 'autocomplete-field', component: AutocompleteFieldComponent},
        {name: 'repeat-field', component: RepeatFieldComponent}
      ]
    }),
    FormlyMaterialModule,
    MatNativeDateModule,
    FormlyMatDatepickerModule,
  ]
})
export class AdlatusFormlyModule {
}
