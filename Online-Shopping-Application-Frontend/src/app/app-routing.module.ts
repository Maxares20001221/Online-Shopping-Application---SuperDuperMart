import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UserHomeComponent } from './components/user/user-home/user-home.component';
import { ProductsComponent } from './components/user/products/products.component';
import { ProductDetailComponent } from './components/user/product-detail/product-detail.component';
import { OrderDetailComponent } from './components/user/order-detail/order-detail.component';
import { AdminHomeComponent } from './components/admin/admin-home/admin-home.component';
import { AdminProductManagementComponent } from './components/admin/admin-product-management/admin-product-management.component';
import { AdminOrderManagementComponent } from './components/admin/admin-order-management/admin-order-management.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'user/home', component: UserHomeComponent, canActivate: [AuthGuard] },
  { path: 'user/products', component: ProductsComponent, canActivate: [AuthGuard] },
  { path: 'user/products/:id', component: ProductDetailComponent, canActivate: [AuthGuard] },
  { path: 'user/orders/:id', component: OrderDetailComponent, canActivate: [AuthGuard] },
  { path: 'admin/home', component: AdminHomeComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/products', component: AdminProductManagementComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/orders', component: AdminOrderManagementComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

