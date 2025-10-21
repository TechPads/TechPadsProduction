package com.core.core.services;

import com.core.core.modules.ClientDetail;

import java.util.List;

public interface ClientDetailService {

    List<ClientDetail> getAllDetails();
    ClientDetail getDetails(Long id);
    ClientDetail createDetails(ClientDetail clientDetail);
    ClientDetail updateDetails(Long id, ClientDetail clientDetail);

}
