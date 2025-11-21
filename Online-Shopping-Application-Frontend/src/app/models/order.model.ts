export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface Order {
  orderId: number;
  datePlaced: string;
  orderStatus: 'Processing' | 'Completed' | 'Canceled';
  totalPrice: number;
  items: OrderItem[];
}

