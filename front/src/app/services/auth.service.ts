import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

// Interfaces
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  role?: string;
  phone: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  phone: string;
  createdAt: string;
  status: string;
  clientDetail?: any;
}

export interface DecodedToken {
  sub: string; // username
  role: string;
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/techPads/store/v1';
  private readonly AUTH_ENDPOINT = `${this.API_URL}/auth`;
  private readonly USERS_ENDPOINT = `${this.API_URL}/users`;

  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_data';

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  private isAuthenticatedSubject: BehaviorSubject<boolean>;
  public isAuthenticated: Observable<boolean>;

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = this.getUserFromStorage();
    this.currentUserSubject = new BehaviorSubject<User | null>(storedUser);
    this.currentUser = this.currentUserSubject.asObservable();

    this.isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
    this.isAuthenticated = this.isAuthenticatedSubject.asObservable();
  }

  // ============================================================
  // =====================  AUTENTICACIÓN  ======================
  // ============================================================

  /**
   * Inicia sesión con username y password
   */
  login(username: string, password: string): Observable<{ token: string }> {
    return this.http
      .post<{ token: string }>(`${this.AUTH_ENDPOINT}/login`, { username, password })
      .pipe(
        tap((response) => {
          if (response && response.token) {
            this.saveToken(response.token);
            this.isAuthenticatedSubject.next(true);

            // Decodificar token para obtener username
            const decoded = this.decodeToken(response.token);
            if (decoded && decoded.sub) {
              // Guardar el username temporalmente para usar en el componente
              localStorage.setItem('temp_username', decoded.sub);
            }
          }
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Registra un nuevo usuario
   */
  register(userData: RegisterRequest): Observable<User> {
    const registerData = { ...userData, role: userData.role || 'USER' };

    return this.http.post<User>(this.USERS_ENDPOINT, registerData).pipe(
      tap((user) => console.log('Usuario registrado:', user)),
      catchError(this.handleError)
    );
  }

  /**
   * Cierra la sesión del usuario
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
    console.log('Sesión cerrada');
  }

  // ============================================================
  // =====================  TOKEN / JWT  ========================
  // ============================================================

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const decoded = this.decodeToken(token);
      const now = Date.now() / 1000;
      return decoded.exp > now;
    } catch {
      return false;
    }
  }

  decodeToken(token: string): DecodedToken {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    } catch (error) {
      console.error('Error decodificando token:', error);
      throw error;
    }
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded = this.decodeToken(token);
      return decoded.role;
    } catch {
      return null;
    }
  }

  // ============================================================
  // =====================  USUARIO  ============================
  // ============================================================

  // Cambia de private a public
  getUserData(username: string): Observable<User> {
    return this.http.get<User>(`${this.USERS_ENDPOINT}/username/${username}`).pipe(
      tap((user) => this.setUser(user)), // Guardar usuario al obtenerlo
      catchError(this.handleError)
    );
  }

  private setUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  // ============================================================
  // =====================  VALIDACIONES  =======================
  // ============================================================

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  isLoggedIn(): boolean {
    return this.hasValidToken();
  }

  // ============================================================
  // =====================  ERRORES  ============================
  // ============================================================

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Ha ocurrido un error desconocido';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 401:
          errorMessage = 'Usuario o contraseña incorrectos';
          break;
        case 403:
          errorMessage = 'No tienes permiso para realizar esta acción';
          break;
        case 404:
          errorMessage = 'Usuario no encontrado';
          break;
        case 409:
          errorMessage = 'El usuario ya existe';
          break;
        case 500:
          errorMessage = 'Error interno del servidor';
          break;
        default:
          errorMessage = error.error?.message || `Error ${error.status}: ${error.message}`;
      }
    }

    console.error('Error en AuthService:', error);
    return throwError(() => new Error(errorMessage));
  }
}
