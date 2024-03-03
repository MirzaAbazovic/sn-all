import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter, withEnabledBlockingInitialNavigation,} from '@angular/router';
import {appRoutes} from './app.routes';
import {HttpClientModule} from '@angular/common/http';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {EffectsModule} from '@ngrx/effects';
import {provideAnimations} from '@angular/platform-browser/animations';
import {environment} from '../environments/environment';
import {AdlatusFormlyModule} from '@adlatus-gui/feature-components/adlatus-formly/adlatus-formly.module';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes, withEnabledBlockingInitialNavigation()),
    importProvidersFrom(HttpClientModule),
    importProvidersFrom(AdlatusFormlyModule),
    importProvidersFrom(StoreModule.forRoot({}), StoreDevtoolsModule.instrument({maxAge: 25}), EffectsModule.forRoot({})),
    provideAnimations(),
    {
      provide: 'environment',
      useValue: environment
    }
  ]
};

