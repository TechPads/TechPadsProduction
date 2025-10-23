import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CitiesDepartmentService, City, Department } from '../../../services/cities-department.service';
import { AuthService, RegisterRequest } from '../../../services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  providers: [CitiesDepartmentService],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Output() switchToLogin = new EventEmitter<void>();
 
  userForm = {
    primerNombre: '',
    segundoNombre: '',
    primerApellido: '',
    segundoApellido: '',
    correo: '',
    password: '',
    username: '',
    telefono: '',
    tipoDireccion: '',
    direccion: '',
    departamento: '',
    ciudad: ''
  };

  departments: Department[] = [];
  cities: City[] = [];
  isLoadingDepartments = false;
  isLoadingCities = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private locationService: CitiesDepartmentService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  /**
   * Carga todos los departamentos al iniciar el componente
   */
  loadDepartments(): void {
    this.isLoadingDepartments = true;
    this.errorMessage = '';
    
    this.locationService.getDepartments().subscribe({
      next: (data) => {
        this.departments = data;
        this.isLoadingDepartments = false;
        console.log('Departamentos cargados:', data);
      },
      error: (err) => {
        console.error('Error al cargar departamentos:', err);
        this.errorMessage = 'No se pudieron cargar los departamentos';
        this.isLoadingDepartments = false;
      }
    });
  }

  /**
   * Se ejecuta cuando el usuario selecciona un departamento
   */
  onDepartmentChange(): void {
    this.userForm.ciudad = '';
    this.cities = [];

    if (!this.userForm.departamento) {
      return;
    }

    this.isLoadingCities = true;
    this.errorMessage = '';
    
    const departmentId = Number(this.userForm.departamento);
    
    this.locationService.getCitiesByDepartment(departmentId).subscribe({
      next: (data) => {
        this.cities = data;
        this.isLoadingCities = false;
        console.log('Ciudades cargadas:', data);
      },
      error: (err) => {
        console.error('Error al cargar ciudades:', err);
        this.errorMessage = 'No se pudieron cargar las ciudades del departamento';
        this.isLoadingCities = false;
      }
    });
  }

  /**
   * Valida el formulario antes de enviarlo
   */
  validateForm(): boolean {
    if (!this.userForm.primerNombre || !this.userForm.primerApellido) {
      this.errorMessage = 'El nombre y apellido son obligatorios';
      return false;
    }

    if (!this.userForm.correo || !this.isValidEmail(this.userForm.correo)) {
      this.errorMessage = 'El correo electrónico no es válido';
      return false;
    }

    if (!this.userForm.username || this.userForm.username.length < 4) {
      this.errorMessage = 'El nombre de usuario debe tener al menos 4 caracteres';
      return false;
    }

    if (!this.userForm.password || this.userForm.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return false;
    }

    if (!this.userForm.telefono) {
      this.errorMessage = 'El teléfono es obligatorio';
      return false;
    }

    if (!this.userForm.departamento || !this.userForm.ciudad) {
      this.errorMessage = 'Por favor selecciona un departamento y una ciudad';
      return false;
    }

    return true;
  }

  /**
   * Valida el formato del email
   */
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Maneja el envío del formulario
   */
  onSubmit(): void {
    // Limpiar mensajes previos
    this.errorMessage = '';
    this.successMessage = '';

    // Validar formulario
    if (!this.validateForm()) {
      return;
    }

    this.isSubmitting = true;

    // Preparar datos para el registro
    const registerData: RegisterRequest = {
      username: this.userForm.username,
      password: this.userForm.password,
      email: this.userForm.correo,
      phone: this.userForm.telefono,
      role: 'USER' // Por defecto, los usuarios registrados tienen rol USER
    };

    // Registrar usuario
    this.authService.register(registerData).subscribe({
      next: (user:any) => {
        console.log('Usuario registrado exitosamente:', user);
        this.successMessage = 'Registro exitoso. Iniciando sesión...';
        
        // Después de registrarse, hacer login automático
        this.autoLogin();
      },
      error: (error:any) => {
        console.error('Error al registrar usuario:', error);
        this.errorMessage = error.message || 'Error al registrar el usuario';
        this.isSubmitting = false;
      }
    });
  }

  /**
   * Inicia sesión automáticamente después del registro
   */
  private autoLogin(): void {
    this.authService.login(this.userForm.username, this.userForm.password).subscribe({
      next: (response) => {
        console.log('Login automático exitoso');
        this.isSubmitting = false;
        
        // Cerrar modal
        this.close.emit();
        
        // Redirigir a la página principal
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error en login automático:', error);
        this.isSubmitting = false;
        this.successMessage = 'Usuario registrado. Por favor inicia sesión.';
        
        // Cambiar a vista de login después de 2 segundos
        setTimeout(() => {
          this.switchToLogin.emit();
        }, 2000);
      }
    });
  }

  /**
   * Cambia a la vista de login
   */
  goToLogin(): void {
    this.switchToLogin.emit();
  }
}