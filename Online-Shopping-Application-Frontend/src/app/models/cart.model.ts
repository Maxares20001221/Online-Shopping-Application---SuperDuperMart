export interface CartItem {
  itemId?: number;
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface CartResponse {
  userId: number;
  totalItems: number;
  totalPrice: number;
  items: CartItem[];
}

