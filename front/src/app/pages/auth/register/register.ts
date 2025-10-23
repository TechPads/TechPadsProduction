import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CitiesDepartmentService, City, Department } from '../../../services/cities-department.service';
import { HttpClientModule } from '@angular/common/http';

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
  errorMessage = '';

  constructor(private locationService: CitiesDepartmentService) {}

  ngOnInit(): void {
    this.loadDepartments();
  }


  loadDepartments(): void {
    this.isLoadingDepartments = true;
    this.errorMessage = '';
    
    this.locationService.getDepartments().subscribe({
      next: (data:any) => {
        this.departments = data;
        this.isLoadingDepartments = false;
        console.log('Departamentos cargados:', data);
      },
      error: (err:any) => {
        console.error('Error al cargar departamentos:', err);
        this.errorMessage = 'No se pudieron cargar los departamentos';
        this.isLoadingDepartments = false;
      }
    });
  }

  
  onDepartmentChange(): void {
    
    this.userForm.ciudad = '';
    this.cities = [];

    
    if (!this.userForm.departamento) {
      return;
    }


    this.isLoadingCities = true;
    this.errorMessage = '';
    
    const departmentId = Number(this.userForm.departamento);
    
    console.log('Cargando ciudades para departamento ID:', departmentId);
    
    this.locationService.getCitiesByDepartment(departmentId).subscribe({
      next: (data:any) => {
        this.cities = data;
        this.isLoadingCities = false;
        console.log('Ciudades cargadas:', data);
      },
      error: (err:any) => {
        console.error('Error al cargar ciudades:', err);
        this.errorMessage = 'No se pudieron cargar las ciudades del departamento';
        this.isLoadingCities = false;
      }
    });
  }

  onSubmit(): void {

    if (!this.userForm.departamento || !this.userForm.ciudad) {
      this.errorMessage = 'Por favor selecciona un departamento y una ciudad';
      return;
    }

    console.log('Form submitted:', this.userForm);
    
    
    this.close.emit();
  }
}