import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Material Modules
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';

// Components
import { LoginComponent } from './components/login/login.component';
import { UserHomeComponent } from './components/user/user-home/user-home.component';
import { ProductsComponent } from './components/user/products/products.component';
import { ProductDetailComponent } from './components/user/product-detail/product-detail.component';
import { OrderDetailComponent } from './components/user/order-detail/order-detail.component';
import { AdminHomeComponent } from './components/admin/admin-home/admin-home.component';
import { AdminProductManagementComponent } from './components/admin/admin-product-management/admin-product-management.component';
import { AdminOrderManagementComponent } from './components/admin/admin-order-management/admin-order-management.component';
import { ShoppingCartComponent } from './components/user/shopping-cart/shopping-cart.component';
import { WatchlistComponent } from './components/user/watchlist/watchlist.component';
import { EditProductComponent } from './components/admin/edit-product/edit-product.component';
import { AddProductComponent } from './components/admin/add-product/add-product.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserHomeComponent,
    ProductsComponent,
    ProductDetailComponent,
    OrderDetailComponent,
    AdminHomeComponent,
    AdminProductManagementComponent,
    AdminOrderManagementComponent,
    ShoppingCartComponent,
    WatchlistComponent,
    EditProductComponent,
    AddProductComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatCardModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatSelectModule,
    MatTabsModule,
    MatChipsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

