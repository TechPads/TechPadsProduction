import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { InventoryService, InventoryItem, Product } from '../../../../services/inventory.service';
import { ProductService } from '../../../../services/product.service';
import { ProviderService, Provider } from '../../../../services/provider.service';

// Validadores personalizados
export class CustomValidators {
  static noFutureDate(control: AbstractControl): { [key: string]: boolean } | null {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(23, 59, 59, 999); // Fin del día actual
    
    const tenYearsAgo = new Date();
    tenYearsAgo.setFullYear(today.getFullYear() - 10);
    tenYearsAgo.setHours(0, 0, 0, 0);

    if (selectedDate > today) {
      return { 'futureDate': true };
    }
    
    if (selectedDate < tenYearsAgo) {
      return { 'tooOldDate': true };
    }
    
    return null;
  }

  static noSqlInjection(control: AbstractControl): { [key: string]: boolean } | null {
    const sqlKeywords = ['SELECT', 'INSERT', 'UPDATE', 'DELETE', 'DROP', 'CREATE', 'ALTER', 'EXEC', 'UNION', 'OR', 'AND'];
    const value = control.value?.toUpperCase() || '';
    
    if (sqlKeywords.some(keyword => value.includes(keyword))) {
      return { 'sqlInjection': true };
    }
    
    return null;
  }

  static onlyNumbers(control: AbstractControl): { [key: string]: boolean } | null {
    const value = control.value;
    if (value && !/^\d+$/.test(value.toString())) {
      return { 'onlyNumbers': true };
    }
    return null;
  }

  static phoneFormat(control: AbstractControl): { [key: string]: boolean } | null {
    const value = control.value;
    if (value && !/^3[0-9]{9}$/.test(value)) {
      return { 'phoneFormat': true };
    }
    return null;
  }

  static decimalNumber(control: AbstractControl): { [key: string]: boolean } | null {
    const value = control.value;
    if (value && !/^\d+(\.\d{1,2})?$/.test(value.toString())) {
      return { 'decimalNumber': true };
    }
    return null;
  }
}

@Component({
  selector: 'app-register-inventory',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-inventory.html',
  styleUrls: ['./register-inventory.css'],
})
export class RegisterInventoryComponent implements OnInit {
  inventoryForm!: FormGroup;
  products: Product[] = [];
  providers: Provider[] = [];
  availableProducts: Product[] = [];
  existingInventory: InventoryItem[] = [];
  previewImage = '';
  toastMessage = '';
  toastType: 'success' | 'error' | '' = '';
  showToast = false;
  loadingProducts = true;
  loadingProviders = true;
  loadingInventory = true;
  submitted = false;
  showNewProviderForm = false;
  fieldErrors: { [key: string]: string } = {};

  constructor(
    private fb: FormBuilder,
    public router: Router,
    private inventoryService: InventoryService,
    private productService: ProductService,
    private providerService: ProviderService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadData();
  }

  initializeForm(): void {
    this.inventoryForm = this.fb.group({
      productCode: ['', [Validators.required]],
      providerSelection: ['existing'],
      existingProviderId: ['', [Validators.required]],
      // Campos para nuevo proveedor
      newProvName: ['', [
        Validators.required, 
        Validators.minLength(2),
        Validators.maxLength(80),
        CustomValidators.noSqlInjection
      ]],
      newProvEmail: ['', [
        Validators.required, 
        Validators.email,
        Validators.maxLength(80),
        CustomValidators.noSqlInjection
      ]],
      newProvPhone: ['', [
        Validators.required, 
        Validators.pattern('^[0-9]{10}$'),
        CustomValidators.phoneFormat
      ]],
      // Campos de inventario
      invStock: ['', [
        Validators.required, 
        Validators.min(1),
        Validators.max(1000000),
        CustomValidators.onlyNumbers
      ]],
      sellingPrice: ['', [
        Validators.required, 
        Validators.min(0.01),
        Validators.max(1000000000),
        CustomValidators.decimalNumber
      ]],
      invDate: [this.getCurrentDate(), [
        Validators.required,
        CustomValidators.noFutureDate
      ]],
    });

    // Escuchar cambios en la selección de proveedor
    this.inventoryForm.get('providerSelection')?.valueChanges.subscribe((value) => {
      this.showNewProviderForm = value === 'new';
      this.updateProviderValidators();
    });

    // Escuchar cambios en el producto seleccionado
    this.inventoryForm.get('productCode')?.valueChanges.subscribe((productCode: string) => {
      const selectedProduct = this.products.find(p => p.proCode === Number(productCode));
      if (selectedProduct) {
        this.previewImage = selectedProduct.proImg || '';
        // Solo establecer precio si no hay valor ingresado
        if (!this.inventoryForm.get('sellingPrice')?.value) {
          this.inventoryForm.patchValue({
            sellingPrice: selectedProduct.proPrice
          });
        }
      }
    });

    // Escuchar cambios en los campos para validación en tiempo real
    this.setupFieldValidation();
  }

  setupFieldValidation(): void {
    const fieldsToWatch = ['invStock', 'sellingPrice', 'invDate', 'newProvName', 'newProvEmail', 'newProvPhone'];
    
    fieldsToWatch.forEach(fieldName => {
      this.inventoryForm.get(fieldName)?.valueChanges.subscribe(() => {
        this.validateField(fieldName);
      });
    });
  }

  validateField(fieldName: string): void {
    const control = this.inventoryForm.get(fieldName);
    if (control && control.errors && (control.dirty || control.touched || this.submitted)) {
      this.fieldErrors[fieldName] = this.getFieldErrorMessage(fieldName, control.errors);
    } else {
      delete this.fieldErrors[fieldName];
    }
  }

  getFieldErrorMessage(fieldName: string, errors: any): string {
    if (errors['required']) return 'Este campo es obligatorio.';
    if (errors['min']) return `El valor mínimo es ${errors['min'].min}.`;
    if (errors['max']) return `El valor máximo es ${errors['max'].max}.`;
    if (errors['minlength']) return `Mínimo ${errors['minlength'].requiredLength} caracteres.`;
    if (errors['maxlength']) return `Máximo ${errors['maxlength'].requiredLength} caracteres.`;
    if (errors['email']) return 'Formato de email inválido.';
    if (errors['pattern']) {
      if (fieldName === 'newProvPhone') return 'El teléfono debe tener 10 dígitos.';
      return 'Formato inválido.';
    }
    if (errors['onlyNumbers']) return 'Solo se permiten números enteros.';
    if (errors['decimalNumber']) return 'Solo se permiten números con máximo 2 decimales.';
    if (errors['futureDate']) return 'La fecha no puede ser futura.';
    if (errors['tooOldDate']) return 'La fecha no puede ser mayor a 10 años atrás.';
    if (errors['sqlInjection']) return 'El texto contiene palabras no permitidas.';
    if (errors['phoneFormat']) return 'El teléfono debe comenzar con 3 y tener 10 dígitos.';
    
    return 'Error de validación.';
  }

  updateProviderValidators(): void {
    const existingProviderId = this.inventoryForm.get('existingProviderId');
    const newProvName = this.inventoryForm.get('newProvName');
    const newProvEmail = this.inventoryForm.get('newProvEmail');
    const newProvPhone = this.inventoryForm.get('newProvPhone');

    if (this.showNewProviderForm) {
      existingProviderId?.clearValidators();
      newProvName?.setValidators([Validators.required, Validators.minLength(2), Validators.maxLength(80), CustomValidators.noSqlInjection]);
      newProvEmail?.setValidators([Validators.required, Validators.email, Validators.maxLength(80), CustomValidators.noSqlInjection]);
      newProvPhone?.setValidators([Validators.required, Validators.pattern('^[0-9]{10}$'), CustomValidators.phoneFormat]);
    } else {
      existingProviderId?.setValidators([Validators.required]);
      newProvName?.clearValidators();
      newProvEmail?.clearValidators();
      newProvPhone?.clearValidators();
      
      // Limpiar campos de nuevo proveedor
      this.inventoryForm.patchValue({
        newProvName: '',
        newProvEmail: '',
        newProvPhone: ''
      });
    }

    existingProviderId?.updateValueAndValidity();
    newProvName?.updateValueAndValidity();
    newProvEmail?.updateValueAndValidity();
    newProvPhone?.updateValueAndValidity();
    
    // Validar campos después de cambiar validadores
    this.validateField('existingProviderId');
    this.validateField('newProvName');
    this.validateField('newProvEmail');
    this.validateField('newProvPhone');
  }

  loadData(): void {
    // Cargar productos
    this.productService.getProducts().subscribe({
      next: (products: Product[]) => {
        this.products = products;
        this.loadingProducts = false;
        this.filterAvailableProducts();
      },
      error: (err: any) => {
        console.error('Error al cargar productos', err);
        this.loadingProducts = false;
        this.showToastMessage('Error al cargar los productos', 'error');
      },
    });

    // Cargar proveedores
    this.providerService.getProviders().subscribe({
      next: (providers: Provider[]) => {
        this.providers = providers;
        this.loadingProviders = false;
      },
      error: (err: any) => {
        console.error('Error al cargar proveedores', err);
        this.loadingProviders = false;
        this.showToastMessage('Error al cargar los proveedores', 'error');
      },
    });

    // Cargar inventario existente
    this.inventoryService.getAllInventory().subscribe({
      next: (inventory: InventoryItem[]) => {
        this.existingInventory = inventory;
        this.loadingInventory = false;
        this.filterAvailableProducts();
      },
      error: (err: any) => {
        console.error('Error al cargar inventario', err);
        this.loadingInventory = false;
        this.showToastMessage('Error al cargar el inventario existente', 'error');
      },
    });
  }

  filterAvailableProducts(): void {
    if (this.products.length === 0 || this.existingInventory.length === 0) {
      this.availableProducts = [...this.products];
      return;
    }

    const existingProductCodes = this.existingInventory.map(item => item.product.proCode);
    this.availableProducts = this.products.filter(
      product => !existingProductCodes.includes(product.proCode)
    );
  }

  getSelectedProduct(): Product | undefined {
    const productCode = this.inventoryForm.get('productCode')?.value;
    if (!productCode) return undefined;
    return this.products.find(p => p.proCode === Number(productCode));
  }

  getMargin(): number | null {
    const sellingPrice = this.inventoryForm.get('sellingPrice')?.value;
    const basePrice = this.getSelectedProduct()?.proPrice;

    if (!sellingPrice || !basePrice || basePrice === 0) {
      return null;
    }

    return ((sellingPrice - basePrice) / basePrice) * 100;
  }

  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  onInputNumber(event: Event, fieldName: string): void {
    const input = event.target as HTMLInputElement;
    let value = input.value;
    
    // Remover caracteres no numéricos excepto punto decimal para precios
    if (fieldName === 'sellingPrice') {
      value = value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
    } else {
      value = value.replace(/[^0-9]/g, '');
    }
    
    input.value = value;
    this.inventoryForm.get(fieldName)?.setValue(value);
    this.validateField(fieldName);
  }

  onInputText(event: Event, fieldName: string): void {
    const input = event.target as HTMLInputElement;
    let value = input.value;
    
    // Limpiar texto de caracteres potencialmente peligrosos
    value = value.replace(/[<>\(\)\-\+\=\*]/g, '');
    input.value = value;
    
    this.inventoryForm.get(fieldName)?.setValue(value);
    this.validateField(fieldName);
  }

  async onSubmit(): Promise<void> {
    this.submitted = true;
    this.validateAllFields();

    if (this.inventoryForm.invalid) {
      const firstError = Object.keys(this.fieldErrors)[0];
      if (firstError) {
        this.showToastMessage(this.fieldErrors[firstError], 'error');
      } else {
        this.showToastMessage('Por favor completa todos los campos correctamente.', 'error');
      }
      return;
    }

    const formData = this.inventoryForm.value;
    const selectedProduct = this.getSelectedProduct();

    if (!selectedProduct) {
      this.showToastMessage('Producto inválido.', 'error');
      return;
    }

    // Verificar que el producto no tenga inventario existente
    const productHasInventory = this.existingInventory.some(
      item => item.product.proCode === selectedProduct.proCode
    );

    if (productHasInventory) {
      this.showToastMessage('Este producto ya tiene inventario registrado. Usa la opción de editar.', 'error');
      return;
    }

    try {
      // 1. Obtener o crear el proveedor
      const provider = await this.getOrCreateProvider(formData);
      
      // 2. Crear el inventario
      const inventoryData: any = {
        invStock: Number(formData.invStock),
        sellingPrice: Number(formData.sellingPrice),
        invDate: formData.invDate,
        product: selectedProduct,
        provider: provider
      };

      this.inventoryService.createInventoryItem(inventoryData).subscribe({
        next: (response) => {
          const message = this.showNewProviderForm 
            ? 'Inventario registrado y proveedor creado correctamente.' 
            : 'Inventario registrado correctamente.';
          this.showToastMessage(message, 'success');
          setTimeout(() => {
            this.router.navigate(['/admin/inventory/list']);
          }, 1500);
        },
        error: (err: any) => {
          this.handleError(err);
        },
      });

    } catch (error: any) {
      this.showToastMessage(error.message, 'error');
    }
  }

  validateAllFields(): void {
    Object.keys(this.inventoryForm.controls).forEach(fieldName => {
      this.validateField(fieldName);
    });
  }

  private getOrCreateProvider(formData: any): Promise<Provider> {
    return new Promise((resolve, reject) => {
      if (this.showNewProviderForm) {
        // Crear nuevo proveedor
        const newProvider: any = {
          provName: formData.newProvName,
          provEmail: formData.newProvEmail,
          provPhone: formData.newProvPhone
        };

        this.providerService.createProvider(newProvider).subscribe({
          next: () => {
            // Buscar el proveedor recién creado por email
            this.providerService.getProviders().subscribe({
              next: (providers) => {
                const createdProvider = providers.find(p => p.provEmail === newProvider.provEmail);
                if (createdProvider) {
                  resolve(createdProvider);
                } else {
                  reject(new Error('No se pudo encontrar el proveedor creado.'));
                }
              },
              error: (err) => reject(err)
            });
          },
          error: (err) => {
            if (err.status === 409) { // Conflicto - email ya existe
              reject(new Error('El email del proveedor ya está registrado.'));
            } else {
              reject(new Error('Error al crear el proveedor.'));
            }
          }
        });
      } else {
        // Usar proveedor existente
        const existingProvider = this.providers.find(p => p.provId === Number(formData.existingProviderId));
        if (existingProvider) {
          resolve(existingProvider);
        } else {
          reject(new Error('Proveedor seleccionado no válido.'));
        }
      }
    });
  }

  private handleError(err: any): void {
    let errorMessage = 'Error al registrar el inventario.';
    
    if (err.error && Array.isArray(err.error)) {
      errorMessage = err.error.join(', ');
    } else if (err.error && typeof err.error === 'string') {
      errorMessage = err.error;
    } else if (err.error?.message) {
      errorMessage = err.error.message;
    }
    
    this.showToastMessage(errorMessage, 'error');
    console.error('Error detallado:', err);
  }

  showToastMessage(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000);
  }
}