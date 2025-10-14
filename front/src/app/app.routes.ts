import { Routes } from '@angular/router';
import { Principal } from './pages/ecommerce/principal/principal';
import { Products } from './pages/ecommerce/products/products';
export const routes: Routes = [
  {
    path: '',
    component: Principal
  },
  {
    path: 'products',
    component: Products
  }
];
