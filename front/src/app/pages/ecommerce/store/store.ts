import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { InventoryService, InventoryItem } from '../../../services/inventory.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-store',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './store.html',
  styleUrls: ['./store.css']
})
export class StoreComponent implements OnInit, OnDestroy {
  inventoryItems: InventoryItem[] = [];
  filteredItems: InventoryItem[] = [];
  private filterSubscription?: Subscription;

  constructor(
    private inventoryService: InventoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadInventory();
   
    this.filterSubscription = this.inventoryService.typeCodeFilter$.subscribe(
      (typeCode: number | null) => {
        console.log('🔄 StoreComponent recibió filtro:', typeCode);
        this.filterByType(typeCode);
      }
    );
  }

  ngOnDestroy(): void {
    this.filterSubscription?.unsubscribe();
  }

  loadInventory(): void {
    this.inventoryService.getAvailableInventory().subscribe({
      next: (data: InventoryItem[]) => {
        console.log('📦 StoreComponent - Inventario cargado:', data.length, 'items');
        this.inventoryItems = data;
        this.filteredItems = data; 
      },
      error: (err: any) => console.error('Error cargando inventario', err)
    });
  }

  filterByType(typeCode: number | null): void {
    console.log('🎯 StoreComponent - Aplicando filtro:', typeCode);
    console.log('📊 Items disponibles para filtrar:', this.inventoryItems.length);
    
    if (typeCode === null) {
      // Mostrar todos los productos activos
      this.filteredItems = this.inventoryItems;
      console.log('📋 Mostrando todos los productos:', this.filteredItems.length);
    } else {
      // Filtrar por tipo
      this.filteredItems = this.inventoryItems.filter(item => {
        const matchesType = item.product.productType.typeCode === typeCode;
        console.log(`🔍 ${item.product.proName} - Tipo: ${item.product.productType.typeCode}, Coincide: ${matchesType}`);
        return matchesType;
      });
      console.log('📋 Productos filtrados:', this.filteredItems.length);
    }
  }

  formatPrice(price: number): string {
    return `$${price.toLocaleString('es-CO')}`;
  }

  viewProductDetail(productCode: number): void {
    this.router.navigate(['/store/product', productCode]);
  }

  addToCart(event: Event, item: InventoryItem): void {
    event.stopPropagation(); 
    console.log('Producto agregado al carrito:', item);
  }
}