import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface City {
  id: number;
  name: string;
  departmentId: number;
}

export interface Department {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class CitiesDepartmentService {
  private readonly API_URL = 'http://localhost:8080/techPads/store/v1'; 
  private readonly CITY_ENDPOINT = `${this.API_URL}/city`;
  private readonly DEPARTMENT_ENDPOINT = `${this.API_URL}/depment`;

  constructor(private http: HttpClient) { }

  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(this.DEPARTMENT_ENDPOINT)
      .pipe(catchError(this.handleError));
  }


  getDepartmentById(id: number): Observable<Department> {
    return this.http.get<Department>(`${this.DEPARTMENT_ENDPOINT}/${id}`)
      .pipe(catchError(this.handleError));
  }

  getCities(): Observable<City[]> {
    return this.http.get<City[]>(this.CITY_ENDPOINT)
      .pipe(catchError(this.handleError));
  }

  getCityById(id: number): Observable<City> {
    return this.http.get<City>(`${this.CITY_ENDPOINT}/${id}`)
      .pipe(catchError(this.handleError));
  }


  getCitiesByDepartment(departmentId: number): Observable<City[]> {
    return this.http.get<City[]>(`${this.CITY_ENDPOINT}/depment/${departmentId}`)
      .pipe(catchError(this.handleError));
  }

  
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Ha ocurrido un error desconocido';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 204:
          errorMessage = 'No se encontraron resultados';
          break;
        case 404:
          errorMessage = error.error || 'Recurso no encontrado';
          break;
        case 500:
          errorMessage = 'Error interno del servidor';
          break;
        default:
          errorMessage = `Código de error: ${error.status}\nMensaje: ${error.message}`;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}