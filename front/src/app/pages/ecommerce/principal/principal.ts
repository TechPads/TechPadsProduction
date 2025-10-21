import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-principal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './principal.html',
  styleUrl: './principal.css'
})
export class Principal {
  showLogin = false;
  showRegister = false;

  constructor(private router: Router) {}

  toggleLogin() {
    this.showLogin = !this.showLogin;
    this.showRegister = false;
  }

  toggleRegister() {
    this.showRegister = !this.showRegister;
    this.showLogin = false;
  }

  openRegisterFromLogin() {
    this.showLogin = false;
    this.showRegister = true;
  }

 
  login() {
    this.router.navigate(['/products']);
  }
}