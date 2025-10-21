import { Component, OnInit } from '@angular/core';
import { InventoryService, InventoryItem, ProductType } from '../../../services/inventory.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-store',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './store.html',
  styleUrls: ['./store.css']
})
export class StoreComponent implements OnInit {
  inventoryItems: InventoryItem[] = [];
  productTypes: ProductType[] = [];
  menuOpen: boolean = false;
  categoriaExpandida: boolean = false;
  selectedTypeCode: number | null = null;

  constructor(
    private inventoryService: InventoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProductTypes();
    this.loadInventory();
  }

  loadProductTypes(): void {
    this.inventoryService.getProductTypes().subscribe({
      next: (data: any) => this.productTypes = data,
      error: (err: any) => console.error('Error cargando tipos de producto', err)
    });
  }

  loadInventory(): void {
    this.inventoryService.getAvailableInventory().subscribe({
      next: (data: any) => this.inventoryItems = data,
      error: (err: any) => console.error('Error cargando inventario', err)
    });
  }

  filterByType(typeCode: number | null): void {
    this.selectedTypeCode = typeCode;
   
    if (typeCode === null) {
      this.loadInventory();
    } else {
      this.inventoryService.getInventoryByType(typeCode).subscribe({
        next: (data: any) => this.inventoryItems = data,
        error: (err: any) => console.error('Error filtrando productos', err)
      });
    }
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria(): void {
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  formatPrice(price: number): string {
    return `$${price.toLocaleString('es-CO')}`;
  }

  goToAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}