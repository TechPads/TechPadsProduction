import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Login } from '../../auth/login/login';
import { Register } from '../../auth/register/register';

@Component({
  selector: 'app-principal',
  standalone: true,
  imports: [CommonModule, Login, Register],
  templateUrl: './principal.html',
  styleUrl: './principal.css'
})
export class Principal {
  showLogin = false;
  showRegister = false;

  toggleLogin(): void {
    this.showLogin = !this.showLogin;
    this.showRegister = false;
  }

  toggleRegister(): void {
    this.showRegister = !this.showRegister;
    this.showLogin = false;
  }

  switchToRegister(): void {
    this.showLogin = false;
    this.showRegister = true;
  }

  closeModals(): void {
    this.showLogin = false;
    this.showRegister = false;
  }
}