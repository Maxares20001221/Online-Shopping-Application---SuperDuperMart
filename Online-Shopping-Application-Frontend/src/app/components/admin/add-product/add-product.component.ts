import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css']
})
export class AddProductComponent {
  productForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private dialogRef: MatDialogRef<AddProductComponent>,
    private snackBar: MatSnackBar
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      description: [''],
      wholesalePrice: [0, [Validators.required, Validators.min(0)]],
      retailPrice: [0, [Validators.required, Validators.min(0)]],
      quantity: [0, [Validators.required, Validators.min(0)]]
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

      this.apiService.createProduct(productData).subscribe({
        next: () => {
          this.snackBar.open('Product created successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to create product', 'Close', { duration: 3000 });
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

