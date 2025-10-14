package com.core.core.controllers;


import com.core.core.modules.Provider;
import com.core.core.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/prov")
public class ProviderController {

    @Autowired
    @Lazy
    private ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<Provider>> getProviders() {
        List<Provider> providers = providerService.getProviders();
        if (providers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProvider(@PathVariable Long id) {
        Provider provider = providerService.getProvider(id);
        if (provider == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Proveedor no encontrado con el id: " + id);
        }
        return ResponseEntity.ok(provider);
    }

    @PostMapping
    public ResponseEntity<?> createProvider(@RequestBody Provider provider) {
        Provider created = providerService.saveProvider(provider);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getProvId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable Long id, @RequestBody Provider provider) {
        Provider updated = providerService.updateProvider(id, provider);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Proveedor no encontrado con el id: " + id);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable Long id) {
        boolean deleted = providerService.deleteProvider(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Proveedor no encontrado con el id: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
