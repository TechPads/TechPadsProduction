import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface ProductType {
  typeCode: number;
  typeName: string;
}

export interface Provider {
  provId: number;
  provEmail: string;
  provName: string;
  provPhone: string;
}

export interface Product {
  proCode: number;
  proName: string;
  proImg: string;
  proPrice: number;
  descript: string;
  proMark: string;
  productType: ProductType;
}

export interface InventoryItem {
  invCode?: number;
  invStock: number;
  sellingPrice: number;
  invDate: string;
  product: Product;
  provider?: Provider;
}

@Injectable({
  providedIn: 'root'  
})
export class InventoryService {
  private apiUrl = environment.apiUrl;
  
  private typeCodeFilterSubject = new Subject<number | null>();
  typeCodeFilter$ = this.typeCodeFilterSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Obtener todo el inventario
  getAllInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/inventory`);
  }

  // Obtener inventario disponible (stock > 0)
  getAvailableInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/inventory`).pipe(
      map(items => items.filter(item => item.invStock > 0))
    );
  }

  // Obtener inventario por tipo de producto
  getInventoryByType(typeCode: number): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/inventory`).pipe(
      map(items => items.filter(item =>
        item.product.productType.typeCode === typeCode && item.invStock > 0
      ))
    );
  }

  // Obtener item de inventario por código
  getInventoryItem(invCode: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.apiUrl}/inventory/${invCode}`);
  }

  // Crear nuevo item de inventario
  createInventoryItem(inventoryData: InventoryItem): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.apiUrl}/inventory`, inventoryData);
  }

  // Actualizar item de inventario
  updateInventoryItem(invCode: number, data: Partial<InventoryItem>): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.apiUrl}/inventory/${invCode}`, data);
  }

  // Eliminar item de inventario
  deleteInventoryItem(invCode: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/inventory/${invCode}`);
  }

  // Obtener tipos de producto
  getProductTypes(): Observable<ProductType[]> {
    return this.http.get<ProductType[]>(`${this.apiUrl}/prodType`);
  }

  // Filtrar por tipo de producto
  filterByTypeCode(typeCode: number | null): void {
    this.typeCodeFilterSubject.next(typeCode);
  }
}