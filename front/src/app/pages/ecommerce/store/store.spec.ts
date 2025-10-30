import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreComponent } from './store';
import { InventoryService, InventoryItem } from '../../../services/inventory.service';
import { Router } from '@angular/router';
import { of, Subject } from 'rxjs';
import 'zone.js';
import 'zone.js/testing';

describe('StoreComponent', () => {
  let component: StoreComponent;
  let fixture: ComponentFixture<StoreComponent>;
  let mockInventoryService: jasmine.SpyObj<InventoryService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockInventory: InventoryItem[] = [
    {
      invCode: 1,
      invStock: 15,
      sellingPrice: 6700000,
      invDate: '2025-10-30',
      status: 'A',
      product: {
        proCode: 1,
        proName: 'iPhone 15 Pro',
        proImg: '',
        proPrice: 6500000,
        descript: '',
        proMark: 'Apple',
        status: 'A',
        productType: {
          typeCode: 1,
          typeName: 'Smartphones'
        }
      },
      provider: {
        provId: 1,
        provEmail: '',
        provName: '',
        provPhone: ''
      }
    },
    {
      invCode: 11,
      invStock: 30,
      sellingPrice: 2300000,
      invDate: '2025-10-15',
      status: 'A',
      product: {
        proCode: 11,
        proName: 'Mac Book Pro 2',
        proImg: '',
        proPrice: 2100000,
        descript: '',
        proMark: 'Apple',
        status: 'A',
        productType: {
          typeCode: 2,
          typeName: 'Laptops'
        }
      },
      provider: {
        provId: 1,
        provEmail: '',
        provName: '',
        provPhone: ''
      }
    },
    {
      invCode: 5,
      invStock: 50,
      sellingPrice: 270000,
      invDate: '2025-10-30',
      status: 'A',
      product: {
        proCode: 5,
        proName: 'Mouse Logitech G502 Hero',
        proImg: '',
        proPrice: 250000,
        descript: '',
        proMark: 'Logitech',
        status: 'A',
        productType: {
          typeCode: 3,
          typeName: 'Accesorios'
        }
      },
      provider: {
        provId: 5,
        provEmail: '',
        provName: '',
        provPhone: ''
      }
    }
  ];

  beforeEach(async () => {
    mockInventoryService = jasmine.createSpyObj('InventoryService', ['getAvailableInventory'], {
      typeCodeFilter$: new Subject<number | null>()
    });

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [StoreComponent],
      providers: [
        { provide: InventoryService, useValue: mockInventoryService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StoreComponent);
    component = fixture.componentInstance;
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar inventario desde el servicio', () => {
    mockInventoryService.getAvailableInventory.and.returnValue(of(mockInventory));

    component.loadInventory();

    expect(mockInventoryService.getAvailableInventory).toHaveBeenCalled();
    expect(component.inventoryItems.length).toBe(3);
    expect(component.filteredItems.length).toBe(3);
  });

  it('debería filtrar productos por tipo (Smartphones)', () => {
    component.inventoryItems = mockInventory;
    component.applyFilter(1); // Smartphones

    expect(component.filteredItems.length).toBe(1);
    expect(component.filteredItems[0].product.productType.typeName).toBe('Smartphones');
  });

  it('debería filtrar productos por tipo (Laptops)', () => {
    component.inventoryItems = mockInventory;
    component.applyFilter(2); // Laptops

    expect(component.filteredItems.length).toBe(1);
    expect(component.filteredItems[0].product.productType.typeName).toBe('Laptops');
  });

  it('debería filtrar productos por tipo (Accesorios)', () => {
    component.inventoryItems = mockInventory;
    component.applyFilter(3); // Accesorios

    expect(component.filteredItems.length).toBe(1);
    expect(component.filteredItems[0].product.productType.typeName).toBe('Accesorios');
  });

  it('debería mostrar todos los productos cuando el filtro es null', () => {
    component.inventoryItems = mockInventory;
    component.applyFilter(null);

    expect(component.filteredItems.length).toBe(3);
  });

  it('debería navegar al detalle del producto', () => {
    const invCode = 11;
    component.viewProductDetail(invCode);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/store/product', invCode]);
  });

  it('debería detener la propagación al agregar al carrito', () => {
    const event = jasmine.createSpyObj('Event', ['stopPropagation']);
    const item = mockInventory[0];

    component.addToCart(event, item);

    expect(event.stopPropagation).toHaveBeenCalled();
  });

  it('debería formatear correctamente los precios', () => {
    const formatted = component.formatPrice(2500000);
    expect(formatted).toBe('$2.500.000');
  });
});
