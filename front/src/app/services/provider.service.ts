import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
export interface Provider {
  provId: number;
  provName: string;
  provEmail: string;
  provPhone: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProviderService {
  private apiUrl = `${environment.apiUrl}/prov`;

  constructor(private http: HttpClient) {}

  getProviders(): Observable<Provider[]> {
    return this.http.get<Provider[]>(this.apiUrl);
  }

  getProviderById(id: number): Observable<Provider> {
    return this.http.get<Provider>(`${this.apiUrl}/${id}`);
  }

  createProvider(provider: Provider): Observable<void> {
    return this.http.post<void>(this.apiUrl, provider);
  }


  updateProvider(id: number, provider: Provider): Observable<Provider> {
    return this.http.put<Provider>(`${this.apiUrl}/${id}`, provider);
  }

  deleteProvider(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
