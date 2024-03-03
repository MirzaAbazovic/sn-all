import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IndividualService} from "@adlatus-gui/business/party-management";
import {Observable} from "rxjs";
import {Individual} from "@adlatus-gui/domain/party-management";
import {ColumnData, EntriesTableComponent} from "../../shared/components/entries-table";
import {Router} from "@angular/router";

@Component({
    selector: 'adlatus-gui-all-individuals',
    standalone: true,
    imports: [CommonModule, EntriesTableComponent],
    templateUrl: './list-individuals.component.html',
    styleUrls: ['./list-individuals.component.scss'],
})

export class ListIndividualsComponent {
    individual$: Observable<Individual[]> = this.individualService.retrieveAllIndividuals();

    columns: ColumnData[] = [
        {key: 'givenName', label: 'First name'},
        {key: 'familyName', label: 'Family name'},
        {key: 'id', label: 'Index'},
    ];

    constructor(private individualService: IndividualService, private router: Router) {
    }

    handleOpenCurrentIndividual(event: any) {
        this.router.navigate(['individuals', event.id]);
    }
}
