import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {ReactiveFormsModule} from "@angular/forms";
import {FieldType, FieldTypeConfig, FormlyModule} from "@ngx-formly/core";
import {MatInputModule} from "@angular/material/input";
import {Observable, of, startWith, switchMap} from "rxjs";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";

@Component({
    selector: 'adlatus-gui-autocomplete-field',
    standalone: true,
    imports: [MatFormFieldModule, MatAutocompleteModule, ReactiveFormsModule, FormlyModule, MatInputModule, NgForOf, AsyncPipe, NgIf],
    templateUrl: './autocomplete-field.component.html',
    styleUrls: ['./autocomplete-field.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AutocompleteFieldComponent extends FieldType<FieldTypeConfig>
    implements OnInit {
    filter!: Observable<string[]>;

    ngOnInit() {
        this.filter = this.formControl.valueChanges.pipe(
            startWith(''),
            switchMap((term) => this.filterData(term))
        );
    }

    private filterData(term: string): Observable<string[]> {
        if (!term) {
            return of(this.field.props['values']);
        }

        const filteredData = this.field.props['values'].filter((item: string) =>
            item.toLowerCase().includes(term.toLowerCase())
        );
        return of(filteredData);
    }
}
