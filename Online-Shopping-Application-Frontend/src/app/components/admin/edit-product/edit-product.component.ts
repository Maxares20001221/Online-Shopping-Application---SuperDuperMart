import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { ProductDTO } from '../../../models/product.model';

@Component({
  selector: 'app-edit-product',
  templateUrl: './edit-product.component.html',
  styleUrls: ['./edit-product.component.css']
})
export class EditProductComponent {
  productForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private dialogRef: MatDialogRef<EditProductComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public product: ProductDTO
  ) {
    this.productForm = this.fb.group({
      name: [product.name, [Validators.required]],
      description: [''],
      wholesalePrice: [product.wholesalePrice || 0, [Validators.required, Validators.min(0)]],
      retailPrice: [product.price, [Validators.required, Validators.min(0)]],
      quantity: [product.stock || 0, [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const productData = {
        name: this.productForm.value.name,
        description: this.productForm.value.description,
        wholesalePrice: this.productForm.value.wholesalePrice,
        retailPrice: this.productForm.value.retailPrice,
        quantity: this.productForm.value.quantity
      };

      this.apiService.updateProduct(this.product.productId, productData).subscribe({
        next: () => {
          this.snackBar.open('Product updated successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to update product', 'Close', { duration: 3000 });
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

