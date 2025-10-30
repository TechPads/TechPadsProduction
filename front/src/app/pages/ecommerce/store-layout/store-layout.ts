import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InventoryService, ProductType } from '../../../services/inventory.service';
import { AuthService } from '../../../services/auth.service'; 

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
  isAdmin: boolean = false; 

  constructor(
    public router: Router,
    public inventoryService: InventoryService,
    public authService: AuthService 
  ) {}

  ngOnInit(): void {
    this.loadProductTypes();
    this.checkUserRole(); 
  }

  loadProductTypes(): void {
    this.inventoryService.getProductTypes().subscribe({
      next: (data: any) => this.productTypes = data,
      error: (err: any) => console.error('Error cargando tipos de producto', err)
    });
  }

  checkUserRole(): void {
    this.isAdmin = this.authService.isAdmin();
    console.log(' Usuario autenticado:', this.authService.isLoggedIn());
    console.log(' Es admin:', this.isAdmin);
    console.log(' Rol:', this.authService.getUserRole());
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  toggleCategoria(): void {
    this.categoriaExpandida = !this.categoriaExpandida;
  }

  filterByType(typeCode: number | null): void {
    console.log('🎯 Filtrando por tipo:', typeCode);
    this.selectedTypeCode = typeCode;
    this.inventoryService.filterByTypeCode(typeCode);
    this.menuOpen = false; 
  }

  goToAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }

 
  logout(): void {
    this.authService.logout();
    this.isAdmin = false;
    this.router.navigate(['/']);
  }
}