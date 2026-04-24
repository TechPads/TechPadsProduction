import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './layout.html',
  styleUrls: ['./layout.css']
})
export class AdminLayoutComponent {
  inventarioExpanded = false;
  productosExpanded = false;
  sidebarOpen = false;

  constructor(public router: Router, private authService: AuthService) {}

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  toggleInventario(): void {
    this.inventarioExpanded = !this.inventarioExpanded;
  }

  toggleProductos(): void {
    this.productosExpanded = !this.productosExpanded;
  }

  goTo(route: string): void {
    this.router.navigate([route]);
  }
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
  
}
