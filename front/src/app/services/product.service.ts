import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductType {
  typeCode: number;
  typeName: string;
  typeDescrip: string;
}

export interface Product {
  proCode: number; 
  proName: string;
  descript: string;
  proImg: string;
  proMark: string;
  proPrice: number;
  productType: ProductType;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/techPads/store/v1';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/product`);
  }

  getProductTypes(): Observable<ProductType[]> {
    return this.http.get<ProductType[]>(`${this.apiUrl}/prodType`);
  }

  getProductsByType(typeId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/product?typeId=${typeId}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/product`, product);
  }
  editProduct(product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/product/${product.proCode}`, product);
  }
}
