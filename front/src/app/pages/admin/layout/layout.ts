import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

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

  constructor(private router: Router) {}

  toggleInventario(): void {
    this.inventarioExpanded = !this.inventarioExpanded;
  }

  toggleProductos(): void {
    this.productosExpanded = !this.productosExpanded;
  }

  goTo(route: string): void {
    this.router.navigate([route]);
  }
  
}
