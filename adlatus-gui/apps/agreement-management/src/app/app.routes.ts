import {Route} from '@angular/router';
import {navigationConfig} from './navigation.config';

const appTitle: string = 'Agreement Management';


export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () => import('@adlatus-gui/feature-components/layouts/admin-layout').then((m) => m.AdminLayoutComponent),
    data: {
      appTitle: appTitle,
      mainNavigation: navigationConfig
    },
    children: [
      {
        path: '',
        loadComponent: () => import('libs/feature-components/src/lib/agreement-management/home').then(m => m.HomeComponent)
      },
      {
        path: 'create',
        loadComponent: () => import('libs/feature-components/src/lib/agreement-management/create-agreement').then(m => m.CreateAgreementComponent)
      },
      {
        path: 'agreements',
        loadComponent: () => import('libs/feature-components/src/lib/agreement-management/list-agreements').then(m => m.ListAgreementsComponent)
      },
      {
        path: 'agreements/:id',
        loadComponent: () => import('libs/feature-components/src/lib/agreement-management/single-agreement').then(m => m.SingleAgreementComponent)
      },
      {
        path: 'agreements/edit/:id',
        loadComponent: () => import('libs/feature-components/src/lib/agreement-management/edit-agreement').then(m => m.EditAgreementComponent)
      }
    ]
  },
  {
    path: '**',
    loadComponent: () => import('libs/feature-components/src/lib/shared/pages/not-found').then((m) => m.NotFoundComponent),
  }
];
