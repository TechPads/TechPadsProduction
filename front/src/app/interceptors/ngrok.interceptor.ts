import { HttpInterceptorFn } from '@angular/common/http';


export const ngrokInterceptor: HttpInterceptorFn = (req, next) => {

  if (req.url.includes('ngrok')) {
    const modifiedReq = req.clone({
      setHeaders: {
        'ngrok-skip-browser-warning': 'true',
        'User-Agent': 'Angular-App'
      }
    });
    return next(modifiedReq);
  }
  
  return next(req);
};