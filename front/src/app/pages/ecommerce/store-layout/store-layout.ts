import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InventoryService, ProductType } from '../../../services/inventory.service';

@Component({
  selector: 'app-store-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './store-layout.html',
  styleUrls: ['./store-layout.css']
})
export class StoreLayoutComponent implements OnInit {
  menuOpen: boolean = false;
  categoriaExpandida: boolean = false;
  productTypes: ProductType[] = [];
  selectedTypeCode: number | null = null;

  constructor(
    private router: Router,
    private inventoryService: InventoryService
  ) {}

  ngOnInit(): void {
    this.loadProductTypes();
  }

  loadProductTypes(): void {
    this.inventoryService.getProductTypes().subscribe({
      next: (data: any) => this.productTypes = data,
      error: (err: any) => console.error('Error cargando tipos de producto', err)
    });
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria(): void {
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  filterByType(typeCode: number | null): void {
    this.selectedTypeCode = typeCode;
    this.inventoryService.filterByTypeCode(typeCode);
  }

  goToAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}