import { Routes } from '@angular/router';
import { Principal } from './pages/ecommerce/principal/principal';
import { StoreLayoutComponent } from './pages/ecommerce/store-layout/store-layout';
import { StoreComponent } from './pages/ecommerce/store/store';
import { AdminLayoutComponent } from './pages/admin/layout/layout';
import { Dashboard } from './pages/admin/dashboard/dashboard';

export const routes: Routes = [
  // Página principal (landing)
  { path: '', component: Principal },
  
  // Rutas de la tienda con layout persistente
  {
    path: 'store',
    component: StoreLayoutComponent,
    children: [
      { path: '', component: StoreComponent },
      // { path: 'producto/:id', component: ProductDetailComponent }
    ]
  },
  
  // Rutas del admin con su propio layout
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