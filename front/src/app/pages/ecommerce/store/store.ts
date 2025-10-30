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
  styleUrls: ['./store.css'],
})
export class StoreComponent implements OnInit, OnDestroy {
  inventoryItems: InventoryItem[] = [];
  filteredItems: InventoryItem[] = [];
  private filterSubscription?: Subscription;
  private inventoryLoaded = false;
  private pendingFilter: number | null = null;

  constructor(
    private inventoryService: InventoryService, 
    private router: Router
  ) {}

  ngOnInit(): void {

    this.loadInventory();
    
    this.filterSubscription = this.inventoryService.typeCodeFilter$.subscribe(
      (typeCode: number | null) => {
        console.log('🔄 StoreComponent recibió filtro:', typeCode);
        
        if (this.inventoryLoaded) {
          this.applyFilter(typeCode);
        } else {
          console.log('⏳ Inventario no cargado, guardando filtro pendiente:', typeCode);
          this.pendingFilter = typeCode;
        }
      }
    );
  }

  ngOnDestroy(): void {
    console.log('🗑️ StoreComponent destruido');
    this.filterSubscription?.unsubscribe();
  }

  loadInventory(): void {
    console.log('📦 Cargando inventario...');
    this.inventoryService.getAvailableInventory().subscribe({
      next: (data: InventoryItem[]) => {
        console.log('✅ Inventario cargado:', data.length, 'items');
        this.inventoryItems = data;
        this.filteredItems = data;
        this.inventoryLoaded = true;

        // ✅ Aplicar filtro pendiente si existe
        if (this.pendingFilter !== null) {
          console.log('🎯 Aplicando filtro pendiente:', this.pendingFilter);
          setTimeout(() => {
            this.applyFilter(this.pendingFilter);
            this.pendingFilter = null;
          }, 100);
        }
      },
      error: (err: any) => {
        console.error('❌ Error cargando inventario', err);
      },
    });
  }

  applyFilter(typeCode: number | null): void {
    console.log('🎯 Aplicando filtro:', typeCode);
    console.log('📊 Total items disponibles:', this.inventoryItems.length);

    if (typeCode === null) {
      // Mostrar todos
      this.filteredItems = this.inventoryItems;
      console.log('📋 Mostrando TODOS los productos:', this.filteredItems.length);
    } else {
      // Filtrar por tipo
      this.filteredItems = this.inventoryItems.filter((item) => {
        const matchesType = item.product.productType.typeCode === typeCode;
        if (matchesType) {
          console.log(`✅ ${item.product.proName} - Tipo: ${item.product.productType.typeCode}`);
        }
        return matchesType;
      });
      console.log('📋 Productos FILTRADOS por tipo', typeCode + ':', this.filteredItems.length);
    }
  }

  filterByType(typeCode: number | null): void {
    this.applyFilter(typeCode);
  }

  formatPrice(price: number): string {
    return `$${price.toLocaleString('es-CO')}`;
  }

  viewProductDetail(invCode: number): void {
    this.router.navigate(['/store/product', invCode]);
  }

  addToCart(event: Event, item: InventoryItem): void {
    event.stopPropagation();
    console.log('🛒 Producto agregado al carrito:', item.product.proName);
  }
}