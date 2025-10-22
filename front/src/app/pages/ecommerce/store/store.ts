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
  private filterSubscription?: Subscription;

  constructor(
    private inventoryService: InventoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadInventory();
   
    this.filterSubscription = this.inventoryService.typeCodeFilter$.subscribe(
      typeCode => this.filterByType(typeCode)
    );
  }

  ngOnDestroy(): void {
    this.filterSubscription?.unsubscribe();
  }

  loadInventory(): void {
    this.inventoryService.getAvailableInventory().subscribe({
      next: (data: any) => this.inventoryItems = data,
      error: (err: any) => console.error('Error cargando inventario', err)
    });
  }

  filterByType(typeCode: number | null): void {
    if (typeCode === null) {
      this.loadInventory();
    } else {
      this.inventoryService.getInventoryByType(typeCode).subscribe({
        next: (data: any) => this.inventoryItems = data,
        error: (err: any) => console.error('Error filtrando productos', err)
      });
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
 
    // this.cartService.addToCart(item);
  }
}