import {Component, OnInit} from '@angular/core';
import {ColumnData, EntriesTableComponent} from "@adlatus-gui/feature-components/shared/components/entries-table";
import {Organization} from "@adlatus-gui/domain/party-management";
import {OrganizationService} from "@adlatus-gui/business/party-management";
import {AsyncPipe, NgIf} from "@angular/common";
import {Observable} from "rxjs";
import {Router} from "@angular/router";


// noinspection JSIgnoredPromiseFromCall
@Component({
    selector: 'adlatus-gui-all-organizations',
    standalone: true,
    imports: [
        EntriesTableComponent,
        AsyncPipe,
        NgIf
    ],
    templateUrl: './list-organizations.component.html',
    styleUrls: ['./list-organizations.component.scss'],
})
export class ListOrganizationsComponent implements OnInit {
    organizations: Observable<Organization[]>;

    columns: (ColumnData)[] = [
        {key: 'name', label: 'Name'},
        {key: 'nameType', label: 'Name type'},
        {key: 'organizationType', label: 'Organization type'},
        {key: 'tradingName', label: 'Trading name'},
    ]

    constructor(private organizationService: OrganizationService, private router: Router) {
        this.organizations = this.organizationService.retrieveAllOrganizations();
    }

    ngOnInit(): void {
    }

    handleOpenCurrentOrganization(event: any) {
        this.router.navigate(['organizations', event.id]);
    }
}
