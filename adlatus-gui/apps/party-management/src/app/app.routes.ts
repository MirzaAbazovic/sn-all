import {Route} from '@angular/router';
import {appTitle, mainNavigation} from "./routing.data";


export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () => import('@adlatus-gui/feature-components/layouts/admin-layout').then((m) => m.AdminLayoutComponent),
    data: {
      appTitle: appTitle,
      mainNavigation: mainNavigation
    },
    children: [
      {
        path: 'create',
        loadComponent: () => import('@adlatus-gui/feature-components/party-management/create-party').then(m => m.CreatePartyComponent)
      },
      {
        path: 'individuals',
        loadComponent: () => import('@adlatus-gui/feature-components/party-management/list-individuals').then(m => m.ListIndividualsComponent)
      },
      {
        path: 'individuals/:id',
        loadComponent: () => import('@adlatus-gui/feature-components/party-management/single-individual').then(m => m.SingleIndividualComponent)
      },
      {
        path: 'organizations',
        loadComponent: () => import('@adlatus-gui/feature-components/party-management/list-organizations').then(m => m.ListOrganizationsComponent)
      },
      {
        path: 'organizations/:id',
        loadComponent: () => import('@adlatus-gui/feature-components/party-management/single-organization').then(m => m.SingleOrganizationComponent)
      },
    ]
  }, {
    path: '**',
    loadComponent: () => import('@adlatus-gui/feature-components/shared/pages/not-found').then((m) => m.NotFoundComponent),
  }
];
