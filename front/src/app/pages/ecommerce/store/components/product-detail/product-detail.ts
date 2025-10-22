import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { InventoryService, InventoryItem } from '../../../../../services/inventory.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.html',
  styleUrls: ['./product-detail.css']
})
export class ProductDetailComponent implements OnInit {
  product: InventoryItem | null = null;
  publicProductData: any = null;
  loading: boolean = true;
  error: boolean = false;
  quantity: number = 1;
  selectedImage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private inventoryService: InventoryService
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
   
    if (productId) {
      this.loadProductDetail(+productId);
    } else {
      this.error = true;
      this.loading = false;
    }
  }

  loadProductDetail(productId: number): void {
    this.inventoryService.getInventoryItem(productId).subscribe({
      next: (data: InventoryItem) => {
        this.product = data;
        this.selectedImage = data.product?.proImg || 'assets/placeholder-product.jpg';
       
        this.publicProductData = {
          nombre: data.product?.proName || 'Producto sin nombre',
          descripcion: data.product?.descript || 'Sin descripción disponible',
          marca: data.product?.proMark || 'Sin marca',
          tipo: data.product?.productType?.typeName || 'Sin categoría',
          precioVenta: data.sellingPrice || 0,
          stockDisponible: data.invStock || 0,
          proveedor: data.provider?.provName || 'Sin proveedor'
        };
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error cargando detalle del producto', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  formatPrice(price: number): string {
    return `$${price.toLocaleString('es-CO')}`;
  }

  increaseQuantity(): void {
    if (this.product && this.product.invStock && this.quantity < this.product.invStock) {
      this.quantity++;
    }
  }

  decreaseQuantity(): void {
    if (this.product && this.product.invStock && this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart(): void {
    if (this.product) {
      console.log('Agregando al carrito:', {
        product: this.publicProductData,
        quantity: this.quantity
      });
    }
  }

  buyNow(): void {
    if (this.product) {
      console.log('Comprar ahora:', {
        product: this.publicProductData,
        quantity: this.quantity
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/store']);
  }

  changeImage(imageUrl: string): void {
    this.selectedImage = imageUrl;
  }
}