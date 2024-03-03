import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FieldWrapper} from "@ngx-formly/core";

@Component({
  selector: 'adlatus-gui-form-section-wrapper',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './form-section-wrapper.component.html',
  styleUrls: ['./form-section-wrapper.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormSectionWrapperComponent extends FieldWrapper {
}
