package com.core.core.services;

import com.core.core.modules.ClientDetail;
import com.core.core.repository.ClientDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientDetailServiceImpl implements ClientDetailService{

    private final ClientDetailRepository clientDetailRepository;

    public ClientDetailServiceImpl(ClientDetailRepository clientDetailRepository) {
        this.clientDetailRepository = clientDetailRepository;
    }

    @Override
    public List<ClientDetail> getAllDetails(){
        return clientDetailRepository.findAll();
    }

    @Override
    public ClientDetail getDetails(Long id){
        return clientDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalles del cliente no encontrado " + id));
    }

    @Override
    public ClientDetail createDetails(ClientDetail clientDetail){
        return clientDetailRepository.save(clientDetail);
    }


    @Override
    public ClientDetail updateDetails(Long id, ClientDetail clientDetail) {
        ClientDetail client = clientDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalles del cliente no encontrado " + id));

        client.setFirstName(clientDetail.getFirstName());
        client.setSecondName(clientDetail.getSecondName());
        client.setFirstLastName(clientDetail.getFirstLastName());
        client.setSecondLastName(clientDetail.getSecondLastName());
        client.setAddress(clientDetail.getAddress());
        client.setDescAddress(clientDetail.getDescAddress());
        client.setCity(clientDetail.getCity());
        client.setDepartment(clientDetail.getDepartment());

        return clientDetailRepository.save(client);
    }



}
