import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { InventoryService, InventoryItem } from '../../../../services/inventory.service';
import { ProviderService, Provider } from '../../../../services/provider.service';

@Component({
  selector: 'app-edit-inventory',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-inventory.html',
  styleUrls: ['./edit-inventory.css'],
})
export class EditInventoryComponent implements OnInit {
  inventoryItem!: InventoryItem;
  inventoryForm!: FormGroup;
  loading = true;
  submitted = false;
  showToast = false;
  errorMessage = '';
  toastMessage = '';
  toastType: 'success' | 'error' | '' = '';
  inventoryCode: number | null = null;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private fb: FormBuilder,
    private inventoryService: InventoryService,
    private providerService: ProviderService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.inventoryCode = Number(id);
        this.loadInventoryData(this.inventoryCode);
      }
    });

    this.inventoryForm = this.fb.group({
      invStock: ['', [Validators.required, Validators.min(0)]],
      sellingPrice: ['', [Validators.required, Validators.min(0)]],
      invDate: ['', Validators.required],
      provId: [{ value: '', disabled: true }],
      provName: ['', [Validators.required, Validators.minLength(2)]],
      provEmail: ['', [Validators.required, Validators.email]],
      provPhone: ['', [Validators.required, Validators.pattern('^[0-9]{7,15}$')]],
    });
  }

  getMargin(): number | null {
    const sellingPrice = this.inventoryForm.get('sellingPrice')?.value;
    const basePrice = this.inventoryItem?.product?.proPrice;

    if (!sellingPrice || !basePrice || basePrice === 0) {
      return null;
    }

    return ((sellingPrice - basePrice) / basePrice) * 100;
  }

  loadInventoryData(invCode: number): void {
  this.loading = true;
  this.inventoryService.getInventoryItem(invCode).subscribe({
    next: (data: InventoryItem) => {
      console.log('📥 Inventario cargado:', data);
      
      this.inventoryItem = {
        ...data,
        status: data.status || 'A', 
        product: data.product ? {
          ...data.product,
          status: data.product.status || 'A' 
        } : data.product,
        provider: data.provider 
      };

      this.inventoryForm.patchValue({
        invStock: data.invStock,
        sellingPrice: data.sellingPrice,
        invDate: data.invDate?.substring(0, 10) ?? '',
        provId: data.provider?.provId ?? '',
        provName: data.provider?.provName ?? '',
        provEmail: data.provider?.provEmail ?? '',
        provPhone: data.provider?.provPhone ?? '',
      });
      this.loading = false;
    },
    error: (err: any) => {
      console.error('Error al cargar el inventario', err);
      this.errorMessage = 'No se pudo cargar la información del inventario.';
      this.showToastMessage('Inventario no encontrado', 'error');
      this.loading = false;
      setTimeout(() => {
        this.router.navigate(['/admin/inventory']);
      }, 2000);
    },
  });
}

  onlyNumbers(event: KeyboardEvent): void {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
    }
  }

  onSubmit(): void {
  this.submitted = true;

  if (this.inventoryForm.invalid) {
    this.showToastMessage('Por favor completa todos los campos correctamente.', 'error');
    return;
  }

  if (!this.inventoryCode) {
    this.showToastMessage('Código de inventario no válido', 'error');
    return;
  }

  const invCode = this.inventoryCode;
  const providerId = this.inventoryItem.provider?.provId;

  
  const updatedInventory: any = {
    invStock: Number(this.inventoryForm.get('invStock')?.value),
    sellingPrice: Number(this.inventoryForm.get('sellingPrice')?.value),
    invDate: this.inventoryForm.get('invDate')?.value,
    status: this.inventoryItem.status || 'A', 
    product: this.inventoryItem.product ? {
      proCode: this.inventoryItem.product.proCode,
      proName: this.inventoryItem.product.proName,
      proImg: this.inventoryItem.product.proImg,
      proPrice: this.inventoryItem.product.proPrice,
      descript: this.inventoryItem.product.descript,
      proMark: this.inventoryItem.product.proMark,
      status: this.inventoryItem.product.status || 'A', 
      productType: this.inventoryItem.product.productType
    } : null,
    provider: this.inventoryItem.provider ? {
      provId: this.inventoryItem.provider.provId,
      provName: this.inventoryItem.provider.provName,
      provEmail: this.inventoryItem.provider.provEmail,
      provPhone: this.inventoryItem.provider.provPhone
     
    } : null
  };

  console.log('📦 Datos a enviar para actualizar:', updatedInventory);

  this.inventoryService.updateInventoryItem(invCode, updatedInventory).subscribe({
    next: (response) => {
      console.log('✅ Inventario actualizado:', response);

      // Actualizar proveedor si cambió
      if (providerId && this.hasProviderChanged()) {
        const updatedProvider: Provider = {
          provId: providerId,
          provName: this.inventoryForm.get('provName')?.value,
          provEmail: this.inventoryForm.get('provEmail')?.value,
          provPhone: this.inventoryForm.get('provPhone')?.value
  
        };

        console.log('📦 Actualizando proveedor:', updatedProvider);

        this.providerService.updateProvider(providerId, updatedProvider).subscribe({
          next: () => {
            this.showToastMessage('Inventario y proveedor actualizados correctamente', 'success');
            setTimeout(() => this.router.navigate(['/admin/inventory/list']), 1500);
          },
          error: (err: any) => {
            console.error('❌ Error al actualizar proveedor:', err);
            const errorMsg =
              typeof err.error === 'string' ? err.error : 'Error al actualizar el proveedor';
            this.showToastMessage(errorMsg, 'error');
          },
        });
      } else {
        this.showToastMessage('Inventario actualizado correctamente', 'success');
        setTimeout(() => this.router.navigate(['/admin/inventory/list']), 1500);
      }
    },
    error: (err: any) => {
      console.error('❌ Error completo:', err);

      let errorMessage = 'Error al actualizar el inventario.';

      if (Array.isArray(err.error)) {
        errorMessage = err.error.join(', ');
      } else if (typeof err.error === 'string') {
        errorMessage = err.error;
      } else if (err.error?.message) {
        errorMessage = err.error.message;
      }

      this.showToastMessage(errorMessage, 'error');
    },
  });
}

  private hasProviderChanged(): boolean {
    const currentProvider = this.inventoryItem.provider;
    if (!currentProvider) return false;

    return (
      this.inventoryForm.get('provName')?.value !== currentProvider.provName ||
      this.inventoryForm.get('provEmail')?.value !== currentProvider.provEmail ||
      this.inventoryForm.get('provPhone')?.value !== currentProvider.provPhone
    );
  }

  showToastMessage(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000);
  }

  goBack(): void {
    this.router.navigate(['/admin/inventory/list']);
  }
}