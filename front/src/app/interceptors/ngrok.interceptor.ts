import { HttpInterceptorFn } from '@angular/common/http';

export const ngrokInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('ngrok')) {
    const modifiedReq = req.clone({
      setHeaders: {
        'ngrok-skip-browser-warning': '69420', 
      }
    });
    return next(modifiedReq);
  }
  
  return next(req);
};