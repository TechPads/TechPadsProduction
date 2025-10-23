import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  @Output() close = new EventEmitter<void>();
  @Output() openRegisterModal = new EventEmitter<void>();

  credentials = {
    email: '',
    password: '',
  };

  constructor(private authService: AuthService) {}

  switchToRegister(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    console.log('switchToRegister called'); 
    this.openRegisterModal.emit();
  }

  onSubmit(): void {
    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.close.emit();
      },
      error: (error) => {
        console.error('Login error:', error);
      },
    });
  }
}
