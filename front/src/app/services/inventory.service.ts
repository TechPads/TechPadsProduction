import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

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
  invCode: number;
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
  private apiUrl = 'http://localhost:8080/techPads/store/v1';
  
  // Subject para comunicar filtros entre componentes
  private typeCodeFilterSubject = new Subject<number | null>();
  typeCodeFilter$ = this.typeCodeFilterSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAvailableInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/inventory`).pipe(
      map(items => items.filter(item => item.invStock > 0))
    );
  }

  getInventoryByType(typeCode: number): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/inventory`).pipe(
      map(items => items.filter(item =>
        item.product.productType.typeCode === typeCode && item.invStock > 0
      ))
    );
  }

  getProductTypes(): Observable<ProductType[]> {
    return this.http.get<ProductType[]>(`${this.apiUrl}/prodType`);
  }

  getInventoryItem(invCode: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.apiUrl}/inventory/${invCode}`);
  }

  filterByTypeCode(typeCode: number | null): void {
    this.typeCodeFilterSubject.next(typeCode);
  }
}