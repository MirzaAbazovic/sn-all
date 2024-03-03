import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {FormlyFieldConfig, FormlyModule} from "@ngx-formly/core";
import {FormlyMaterialModule} from "@ngx-formly/material";
import {MatButtonModule} from "@angular/material/button";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [NgIf, ReactiveFormsModule, FormlyModule, FormlyMaterialModule, MatButtonModule],
  selector: 'adlatus-gui-entry-form',
  styleUrls: ['./entry-form.component.scss'],
  templateUrl: './entry-form.component.html',
})
export class EntryFormComponent implements OnChanges {

  @Input({required: false}) title: string | null = null;
  @Input({required: false}) description: string | null = null;
  @Input({required: true}) model: object = {};
  @Input({required: true}) fields: FormlyFieldConfig[] = [];
  @Output() formSubmitHandler: EventEmitter<typeof this.model> = new EventEmitter<typeof this.model>();

  form: FormGroup = new FormGroup<typeof this.model>({});

  constructor() {
  }

  onSubmit(model: object) {
    if (this.form.valid) {
      this.formSubmitHandler.emit(model);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.fields) {
      this.form = new FormGroup<typeof this.model>({});
    }
  }
}
