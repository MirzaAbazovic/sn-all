import {APP_INITIALIZER, ApplicationConfig, importProvidersFrom} from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
} from '@angular/router';
import { appRoutes } from './app.routes';
import {StoreModule} from "@ngrx/store";
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import {EffectsModule} from "@ngrx/effects";
import {HttpClientModule} from "@angular/common/http";
import {provideAnimations} from "@angular/platform-browser/animations";
import { environment } from '../environments/environment';
import {AppInitService} from "./app-init.service";


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes, withEnabledBlockingInitialNavigation()),
    importProvidersFrom(HttpClientModule),
    importProvidersFrom(StoreModule.forRoot({}), StoreDevtoolsModule.instrument({maxAge: 25}), EffectsModule.forRoot({})),
    provideAnimations(),
    {
      provide: 'environment',
      useValue: environment
    },
    {
      provide: APP_INITIALIZER,
      useFactory: (appInitService: AppInitService) => () => appInitService.initApp(),
      deps: [AppInitService],
      multi: true
    }
  ],
};
