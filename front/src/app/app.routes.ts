import { Routes } from '@angular/router';
import { Principal } from './pages/ecommerce/principal/principal';
import { StoreLayoutComponent } from './pages/ecommerce/store-layout/store-layout';
import { ProductDetailComponent } from './pages/ecommerce/store/components/product-detail/product-detail';
import { StoreComponent } from './pages/ecommerce/store/store';
import { AdminLayoutComponent } from './pages/admin/layout/layout';
import { Dashboard } from './pages/admin/dashboard/dashboard';

export const routes: Routes = [
  { path: '', component: Principal },
  
  {
    path: 'store',
    component: StoreLayoutComponent,
    children: [
      { path: '', component: StoreComponent },
      { path: 'product/:id', component: ProductDetailComponent }
    ]
  },
  
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