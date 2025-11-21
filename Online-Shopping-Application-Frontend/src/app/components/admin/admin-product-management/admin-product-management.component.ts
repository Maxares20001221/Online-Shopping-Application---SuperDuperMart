import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ApiService } from '../../../services/api.service';
import { ProductDTO } from '../../../models/product.model';
import { AddProductComponent } from '../add-product/add-product.component';
import { EditProductComponent } from '../edit-product/edit-product.component';

@Component({
  selector: 'app-admin-product-management',
  templateUrl: './admin-product-management.component.html',
  styleUrls: ['./admin-product-management.component.css']
})
export class AdminProductManagementComponent implements OnInit {
  products: ProductDTO[] = [];
  displayedColumns: string[] = ['productId', 'name', 'price', 'stock', 'wholesalePrice', 'actions'];

  constructor(
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.apiService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products;
      },
      error: (error) => {
        this.snackBar.open('Failed to load products', 'Close', { duration: 3000 });
      }
    });
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/user/products', productId]);
  }

  editProduct(product: ProductDTO): void {
    const dialogRef = this.dialog.open(EditProductComponent, {
      width: '500px',
      data: product
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  addProduct(): void {
    const dialogRef = this.dialog.open(AddProductComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }
}

