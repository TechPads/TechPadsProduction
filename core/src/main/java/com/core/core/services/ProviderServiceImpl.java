package com.core.core.services;

import com.core.core.modules.Product;
import com.core.core.modules.Provider;
import com.core.core.repository.ProviderRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Lazy
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public List<Provider> getProviders(){
        return providerRepository.findAll();
    }

    @Override
    public Provider getProvider(Long id){
        return providerRepository.findById(id).orElse(null);
    }

    @Override
    public Provider saveProvider(Provider provider){
        return providerRepository.save(provider);
    }

    @Override
    public Provider updateProvider(Long id, Provider provider){
        return providerRepository.findById(id)
            .map(existingType -> {
                existingType.setProvName(provider.getProvName());
                existingType.setProvEmail(provider.getProvEmail());
                existingType.setProvPhone(provider.getProvPhone());
                return providerRepository.save(existingType);
            })
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
    }

    @Override
    public boolean deleteProvider(Long id){
        Optional<Provider> optional = providerRepository.findById(id);
        if(optional.isPresent()){
            providerRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
