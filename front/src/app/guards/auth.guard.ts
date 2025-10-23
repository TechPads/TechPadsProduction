import { inject } from '@angular/core';
import { 
  CanActivateFn, 
  Router, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot 
} from '@angular/router';
import { AuthService } from '../services/auth.service';

export const AuthGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    const requiredRoles = route.data['roles'] as Array<string>;
    
    if (requiredRoles) {
      const userRole = authService.getUserRole();
      
      if (userRole && requiredRoles.includes(userRole)) {
        return true;
      } else {
        console.warn('Acceso denegado: rol insuficiente');
        router.navigate(['/']);
        return false;
      }
    }
    
    return true;
  }
  
  console.log('Usuario no autenticado, redirigiendo al login');
  router.navigate(['/'], { queryParams: { returnUrl: state.url } });
  return false;
};