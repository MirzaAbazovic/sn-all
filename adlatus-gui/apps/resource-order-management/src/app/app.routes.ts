import {Route} from '@angular/router';
import {navigationConfig} from "./navigation.config";

export const appRoutes: Route[] = [

  {
    path: '',
    loadComponent: () => import('@adlatus-gui/feature-components/layouts/admin-layout').then(m => m.AdminLayoutComponent),
    data: {
      appTitle: 'Resource Ordering Management',
      mainNavigation: navigationConfig
    },
    children: [
      {
        path: '',
        loadComponent: () => import('@adlatus-gui/feature-components/resource-order-management/home').then(m => m.HomeComponent)
      },
      {
        path: 'create',
        loadComponent: () =>
          import('@adlatus-gui/feature-components/resource-order-management/create-resource-order').then(m => m.CreateResourceOrderComponent)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('@adlatus-gui/feature-components/resource-order-management/list-resource-orders').then(m => m.ListResourceOrdersComponent)
      },
      {
        path: 'orders/:id',
        loadComponent: () =>
          import('@adlatus-gui/feature-components/resource-order-management/single-resource-order').then(m => m.SingleResourceOrderComponent)
      }
    ]
  },
  {
    path: '**',
    loadComponent: () => import('libs/feature-components/src/lib/shared/pages/not-found').then((m) => m.NotFoundComponent),
  }
];
