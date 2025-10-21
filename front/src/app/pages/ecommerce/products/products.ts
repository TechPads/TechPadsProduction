import { Component, OnInit } from '@angular/core';
import { ProductService, Product, ProductType } from '../../../services/product.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.html',
  styleUrls: ['./products.css']
})
export class Products implements OnInit {
  productos: Product[] = [];
  productTypes: ProductType[] = [];
  menuOpen: boolean = false;
  categoriaExpandida: boolean = false;
  selectedTypeId: number | null = null;

  constructor(private productService: ProductService, private router: Router) {}

  ngOnInit(): void {
    this.loadProductTypes();
    this.loadProducts();
  }

  loadProductTypes(): void {
    this.productService.getProductTypes().subscribe({
      next: (data) => this.productTypes = data,
      error: (err) => console.error('Error cargando tipos de producto', err)
    });
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (data) => this.productos = data,
      error: (err) => console.error('Error cargando productos', err)
    });
  }

  filterByType(typeId: number | null): void {
    this.selectedTypeId = typeId;
    
    if (typeId === null) {
      this.loadProducts();
    } else {
      this.productService.getProductsByType(typeId).subscribe({
        next: (data) => this.productos = data,
        error: (err) => console.error('Error filtrando productos', err)
      });
    }
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria(): void {
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  toggleFavorito(producto: Product): void {
    // Implementar lógica de favoritos si es necesario
  }

  formatPrecio(precio: number): string {
    return `$${precio.toLocaleString('es-CO')}`;
  }

  goToAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}