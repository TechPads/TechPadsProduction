import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService, Product } from '../../../../services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  showDeleteModal = false;
  productToDelete: Product | null = null;
  toastMessage = '';
  toastType: 'success' | 'error' | '' = '';
  showToast = false;

  constructor(
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getProducts().subscribe({
      next: (products: Product[]) => {
        this.products = products;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar productos', err);
        this.showToastMessage('Error al cargar los productos', 'error');
        this.loading = false;
      }
    });
  }

  editProduct(product: Product): void {
    this.router.navigate(['/admin/products/edit', product.proCode]);
  }

  confirmDelete(product: Product): void {
    this.productToDelete = product;
    this.showDeleteModal = true;
  }

  cancelDelete(): void {
    this.showDeleteModal = false;
    this.productToDelete = null;
  }

  deleteProduct(): void {
    if (!this.productToDelete) return;

    this.productService.deleteProductById(this.productToDelete.proCode).subscribe({
      next: () => {
        this.showToastMessage('Producto eliminado correctamente', 'success');
        this.products = this.products.filter(p => p.proCode !== this.productToDelete?.proCode);
        this.showDeleteModal = false;
        this.productToDelete = null;
      },
      error: (err: any) => {
        const errorMsg = err?.error?.message || 'Error al eliminar el producto';
        this.showToastMessage(errorMsg, 'error');
        this.showDeleteModal = false;
        this.productToDelete = null;
      }
    });
  }

  showToastMessage(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000);
  }

  formatPrice(price: number): string {
    return `$${price.toLocaleString('es-CO')}`;
  }

  goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}