import {Route} from '@angular/router';
import {appTitle, mainNavigation} from "./routing.data";

export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () => import('@adlatus-gui/feature-components/layouts/admin-layout').then((m) => m.AdminLayoutComponent),
    data: {
      title: appTitle,
      mainNavigation: mainNavigation
    },
    children: [
      {
        path: 'resource-specifications',
        children: [
          {
            path: '',
            loadComponent: () => import('libs/feature-components/src/lib/resource-catalog-management/list-resource-specifications').then(m => m.ListResourceSpecificationsComponent)
          },
          {
            path: 'create',
            loadComponent: () => import('libs/feature-components/src/lib/resource-catalog-management/create-resource-specification').then(m => m.CreateResourceSpecificationComponent)
          },
          {
            path: ':id',
            loadComponent: () => import('libs/feature-components/src/lib/resource-catalog-management/single-resource-specification').then(m => m.SingleResourceSpecificationComponent)
          },
          {
            path: ':id/edit',
            loadComponent: () => import('libs/feature-components/src/lib/resource-catalog-management/edit-resource-specification').then(m => m.EditResourceSpecificationComponent)
          }
        ]
      }
    ]
  }, {
    path: '**',
    loadComponent: () => import('libs/feature-components/src/lib/shared/pages/not-found').then((m) => m.NotFoundComponent),
  }
];
