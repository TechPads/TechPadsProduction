import { Routes } from '@angular/router';
import { Principal } from './pages/ecommerce/principal/principal';
import { Products } from './pages/ecommerce/products/products';
import { AdminLayoutComponent } from './pages/admin/layout/layout';
import { Dashboard } from './pages/admin/dashboard/dashboard';

export const routes: Routes = [
  { path: '', component: Principal },
  { path: 'products', component: Products },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    children: [
      { path: 'dashboard', component: Dashboard },
      {
        path: 'products/create',
        loadComponent: () =>
          import('./pages/admin/products/create-product/create-product').then(
            (m) => m.CreateProductComponent
          ),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
];
