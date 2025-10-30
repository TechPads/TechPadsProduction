import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreLayoutComponent } from './store-layout';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { InventoryService } from '../../../services/inventory.service';
import { AuthService } from '../../../services/auth.service';

describe('StoreLayoutComponent', () => {
  let component: StoreLayoutComponent;
  let fixture: ComponentFixture<StoreLayoutComponent>;
  let mockRouter: any;
  let mockInventoryService: any;
  let mockAuthService: any;

  beforeEach(async () => {
    mockRouter = {
      url: '/store',
      navigate: jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true))
    };

    mockInventoryService = {
      getProductTypes: jasmine.createSpy('getProductTypes').and.returnValue(of([{ id: 1, name: 'Bebidas' }])),
      filterByTypeCode: jasmine.createSpy('filterByTypeCode')
    };

    mockAuthService = {
      isAdmin: jasmine.createSpy('isAdmin').and.returnValue(true),
      isLoggedIn: jasmine.createSpy('isLoggedIn').and.returnValue(true),
      getUserRole: jasmine.createSpy('getUserRole').and.returnValue('admin'),
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [StoreLayoutComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: InventoryService, useValue: mockInventoryService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StoreLayoutComponent);
    component = fixture.componentInstance;
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar tipos de producto al inicializar', () => {
    fixture.detectChanges();
    expect(mockInventoryService.getProductTypes).toHaveBeenCalled();
    expect(component.productTypes.length).toBeGreaterThan(0);
  });

  it('debería verificar el rol del usuario al inicializar', () => {
    fixture.detectChanges();
    expect(mockAuthService.isAdmin).toHaveBeenCalled();
    expect(mockAuthService.isLoggedIn).toHaveBeenCalled();
    expect(mockAuthService.getUserRole).toHaveBeenCalled();
    expect(component.isAdmin).toBeTrue();
  });

  it('debería cerrar sesión y navegar al inicio', () => {
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
