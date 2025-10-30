import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { switchMap } from 'rxjs/operators';
export interface ProductType {
  typeCode: number;
  typeName: string;
  typeDescrip: string;
}

export interface Product {
  proCode: number;
  proName: string;
  proImg: string;
  proPrice: number;
  descript: string;
  proMark: string;
  status: string;
  productType: ProductType;
}

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = environment.apiUrl;

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

  getProductById(proCode: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/product/${proCode}`);
  }

  // Cambiar a borrado lógico usando PUT
  deleteProductById(proCode: number): Observable<Product> {
    // Primero obtenemos el producto actual
    return this.getProductById(proCode).pipe(
      switchMap((product: Product) => {
        // Actualizamos el status a 'I' (inactivo)
        const updatedProduct = {
          ...product,
          status: 'I',
        };
        return this.http.put<Product>(`${this.apiUrl}/product/${proCode}`, updatedProduct);
      })
    );
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/product`, product);
  }

  updateProduct(proCode: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/product/${proCode}`, product);
  }

  // Método específico para actualizar solo el status
  updateProductStatus(proCode: number, status: string): Observable<Product> {
    return this.getProductById(proCode).pipe(
      switchMap((product: Product) => {
        const updatedProduct = {
          ...product,
          status: status,
        };
        return this.http.put<Product>(`${this.apiUrl}/product/${proCode}`, updatedProduct);
      })
    );
  }
}
