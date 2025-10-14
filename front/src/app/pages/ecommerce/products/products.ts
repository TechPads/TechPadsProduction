import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products {
  menuOpen = false;
  categoriaExpandida = false;

  productos = [
    {
      id: 1,
      nombre: 'ROG Astral GeForce RTX™ 5090 32GB GDDR7 OC Edition',
      precio: 1900000,
      imagen: 'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      colores: []
    },
    {
      id: 2,
      nombre: 'ROG RYUJIN III 360 ARGB Extreme White Edition',
      precio: 2200000,
      imagen: 'https://images.unsplash.com/photo-1635514569146-9a9607ecf303?auto=format&fit=crop&w=800&q=80',
      favorito: true,
      colores: []
    },
    {
      id: 3,
      nombre: 'ROG Keris Wireless AimPoint',
      precio: 540000,
      imagen: 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      colores: ['red', 'blue']
    },
    {
      id: 4,
      nombre: 'ExpertBook P5 (P5405)',
      precio: 3300000,
      imagen: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      colores: []
    },
    {
      id: 5,
      nombre: 'Diadema Usb Logitech H390',
      precio: 149900,
      imagen: 'https://images.unsplash.com/photo-1577174881658-0f30157f5fd4?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      colores: ['pink']
    },
    {
      id: 6,
      nombre: 'ROG MAXIMUS Z890 HERO',
      precio: 989000,
      imagen: 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      colores: []
    }
  ];

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria() {
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  toggleFavorito(producto: any) {
    producto.favorito = !producto.favorito;
  }

  formatPrecio(precio: number): string {
    return '$' + precio.toLocaleString('es-CO');
  }
}