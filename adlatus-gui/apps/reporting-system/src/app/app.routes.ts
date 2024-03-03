import { Route } from '@angular/router';
import {appTitle, mainNavigation, secondaryNavigation} from "./routing.data";

export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () => import('@adlatus-gui/feature-components/layouts/reporting-system-layout').then((m) => m.ReportingSystemLayoutComponent),
    data: {
      appTitle: appTitle,
      mainNavigation: mainNavigation
    },
    children: [
      {
        path: "",
        loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/views/landing-page/landing-page.component').then(m => m.LandingPageComponent),
      },
      {
        path: 'orders',
        loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/layouts/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
        data: {
          secondaryNavigation: secondaryNavigation
        },
        children: [
          {
            path: '',
            loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/views/orders-home/orders-home.component').then(m => m.OrdersHomeComponent),
          },
          {
            path: 'active',
            loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/views/list-active-orders/list-active-orders.component').then(m => m.ListActiveOrdersComponent),
          },
          {
            path: 'archive',
            loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/views/list-archived-orders/list-archived-orders.component').then(m => m.ListArchivedOrdersComponent),
          },
          {
            path: ':orderNumber',
            loadComponent: () => import('@adlatus-gui/feature-components/reporting-system/views/order-details/order-details.component').then(m => m.OrderDetailsComponent),
          }
        ]
      }
    ]
  }, {
    path: '**',
    loadComponent: () => import('@adlatus-gui/feature-components/shared/pages/not-found').then((m) => m.NotFoundComponent),
  }
];
