import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
} from '@angular/router';
import {appRoutes} from './app.routes';
import {environment} from "../environments/environment";
import {provideAnimations} from '@angular/platform-browser/animations';
import {AdlatusFormlyModule} from "@adlatus-gui/feature-components/adlatus-formly/adlatus-formly.module";
import {HttpClientModule} from "@angular/common/http";

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(AdlatusFormlyModule),
    importProvidersFrom(HttpClientModule),
    provideRouter(appRoutes, withEnabledBlockingInitialNavigation()),
    {
      provide: 'environment',
      useValue: environment
    },
    provideAnimations()
  ],
};
