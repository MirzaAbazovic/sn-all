import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
} from '@angular/router';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {EffectsModule} from '@ngrx/effects';
import {AdlatusFormlyModule} from '@adlatus-gui/feature-components/adlatus-formly/adlatus-formly.module';
import {provideAnimations} from '@angular/platform-browser/animations';

import {appRoutes} from './app.routes';
import {environment} from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(appRoutes, withEnabledBlockingInitialNavigation()),
    importProvidersFrom(HttpClientModule),
    importProvidersFrom(AdlatusFormlyModule),
    importProvidersFrom(StoreModule.forRoot({}), StoreDevtoolsModule.instrument({maxAge: 25}), EffectsModule.forRoot({})),
    provideAnimations(),
    {
      provide: 'environment',
      useValue: environment
    },
  ],
};
