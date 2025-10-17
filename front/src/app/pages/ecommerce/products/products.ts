import { Component } from '@angular/core';
import { ProductService, Product } from '../../../services/product.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.html',
  styleUrls: ['./products.css']
})
export class Products {
  productos: Product[] = [];
  menuOpen: boolean = false;
  categoriaExpandida: boolean = false; 

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (data) => this.productos = data,
      error: (err) => console.error('Error cargando productos', err)
    });
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria(): void { 
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  toggleFavorito(producto: any): void {
    producto.favorito = !producto.favorito;
  }

  formatPrecio(precio: number): string {
    return `$${precio.toLocaleString('es-CO')}`;
  }
}
