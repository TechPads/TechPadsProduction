package com.core.core.services;

import com.core.core.modules.Provider;

import java.util.List;

public interface ProviderService {

    List<Provider> getProviders();
    Provider getProvider(Long id);
    Provider saveProvider(Provider provider);
    Provider updateProvider(Long id, Provider provider);
    boolean deleteProvider(Long id);
}
