import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductType {
  typeCode: number;
  typeName: string;
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

@Injectable({
  providedIn: 'root'   
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/techPads/store/v1/product';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }
}
