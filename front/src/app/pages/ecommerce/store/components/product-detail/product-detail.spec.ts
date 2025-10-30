import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { ProductDetailComponent } from './product-detail';
import { InventoryService, InventoryItem } from '../../../../../services/inventory.service';

describe('ProductDetailComponent', () => {
  let component: ProductDetailComponent;
  let fixture: any;
  let inventarioSpy: jasmine.SpyObj<InventoryService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // Creamos mocks (simulaciones) de los servicios
    const mockInventoryService = jasmine.createSpyObj('InventoryService', ['getInventoryItem']);
    const mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent],
      providers: [
        { provide: InventoryService, useValue: mockInventoryService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map([['id', '1']]) } // simulamos un parámetro "id=1"
          }
        },
        provideHttpClient() 
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductDetailComponent);
    component = fixture.componentInstance;
    inventarioSpy = TestBed.inject(InventoryService) as jasmine.SpyObj<InventoryService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar correctamente los detalles del producto', () => {
    const productoMock: InventoryItem = {
      invCode: 1,
      invStock: 10,
      sellingPrice: 50000,
      product: {
        proName: 'Camiseta',
        proImg: 'img.jpg',
        descript: 'Camiseta de algodón',
        proMark: 'Marca X',
        productType: { typeName: 'Ropa' }
      },
      provider: { provName: 'Proveedor A' }
    } as any;

    inventarioSpy.getInventoryItem.and.returnValue(of(productoMock));

    component.ngOnInit();

    expect(inventarioSpy.getInventoryItem).toHaveBeenCalledWith(1);
    expect(component.product).toEqual(productoMock);
    expect(component.loading).toBeFalse();
    expect(component.error).toBeFalse();
    expect(component.publicProductData.nombre).toBe('Camiseta');
  });

  it('debería manejar correctamente un error al cargar el producto', () => {
    inventarioSpy.getInventoryItem.and.returnValue(throwError(() => new Error('Error cargando')));

    component.ngOnInit();

    expect(component.error).toBeTrue();
    expect(component.loading).toBeFalse();
  });

  it('debería aumentar y disminuir la cantidad correctamente', () => {
    component.product = { invStock: 5 } as any;
    component.quantity = 2;

    component.increaseQuantity();
    expect(component.quantity).toBe(3);

    component.decreaseQuantity();
    expect(component.quantity).toBe(2);
  });

  it('debería navegar de regreso cuando se llama goBack()', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/store']);
  });

  it('debería cambiar la imagen seleccionada', () => {
    component.changeImage('nueva-imagen.jpg');
    expect(component.selectedImage).toBe('nueva-imagen.jpg');
  });
});
