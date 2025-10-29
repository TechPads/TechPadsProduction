import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ngrokInterceptor } from './interceptors/ngrok.interceptor';  
import { routes } from './app.routes'; 

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
        ngrokInterceptor,   
        AuthInterceptor     
      ])
    )
  ]
};