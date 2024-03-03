import {ChangeDetectionStrategy, Component} from '@angular/core';
import {NgFor} from '@angular/common';
import {MatButtonModule} from "@angular/material/button";
import {FieldArrayType, FormlyModule} from "@ngx-formly/core";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'adlatus-gui-repeat-field',
  standalone: true,
  imports: [NgFor, MatButtonModule, FormlyModule, MatIconModule],
  templateUrl: './repeat-field.component.html',
  styleUrls: ['./repeat-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RepeatFieldComponent extends FieldArrayType {}
