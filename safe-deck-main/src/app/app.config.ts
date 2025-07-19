import { provideEventPlugins } from "@taiga-ui/event-plugins";
import { provideAnimations } from "@angular/platform-browser/animations";
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { JwtInterceptor } from "./services/authorization/jwt-interceptor";

export const appConfig: ApplicationConfig = {
   providers: [
      provideHttpClient(
         withInterceptorsFromDi(),
      ),
      provideAnimations(), 
      provideZoneChangeDetection({ eventCoalescing: true }),
      provideRouter(routes),
      {
         provide: HTTP_INTERCEPTORS,
         useClass: JwtInterceptor,
         multi: true
      },
      provideEventPlugins()
   ]
};
