package com.example.tingesoM.Service.Interface.Users;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;

import java.util.List;

public interface ClientService {
    Client save(CreateClientDto client);
    Client findByName(String name);
    Client findById(Long id);
    List<Client> findAll();
    List<Client> findDelayedClient();
    void updateCustomer(Client client, Long id);
    void deleteCustomer(Client client);
    void updateStatusCustomer(Long idCustomer);

}
