export interface Product {
  productId: number;
  name: string;
  price: number;
  stock: number | null;
  wholesalePrice?: number | null;
  description?: string;
}

export interface ProductDTO {
  productId: number;
  name: string;
  description?: string | null;
  price: number;
  stock: number | null;
  wholesalePrice?: number | null;
}

