import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AsyncPipe, NgIf} from '@angular/common';
import {Observable} from 'rxjs';
import {ColumnData, EntriesTableComponent} from '@adlatus-gui/feature-components/shared/components/entries-table';
import {Agreement} from '@adlatus-gui/domain/agreement-management';
import {AgreementService} from '@adlatus-gui/business/agreement-management';
import {Router} from "@angular/router";
import {EntryFormComponent} from "libs/feature-components/src/lib/shared/components/entry-form";
import {MatCardModule} from "@angular/material/card";

@Component({
    selector: 'adlatus-gui-list-agreements',
    standalone: true,
    imports: [NgIf, EntriesTableComponent, AsyncPipe, EntryFormComponent, MatCardModule],
    templateUrl: './list-agreements.component.html',
    styleUrls: ['./list-agreements.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListAgreementsComponent {
    agreements$: Observable<Agreement[]> = this.agreementService.retrieveAllAgreements();

    columns: ColumnData[] = [
        {key: 'name', label: 'Name'},
        {key: 'agreementType', label: 'Type'},
        {key: 'documentNumber', label: 'Doc. no.'},
    ];

    constructor(private agreementService: AgreementService, private router: Router) {
    }

    openAgreement(event: object) {
        this.router.navigate(['agreements', (<Agreement>event).id!]);
    }
}
