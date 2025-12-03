package com.example.tingesoM.Service.ServiceImpl.Users;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Service.Interface.Users.ClientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    ClientRepositorie clientRepositorie;

    @Autowired
    LoanRepositorie loanRepositorie;

    //Create
    @Override
    public Client save(CreateClientDto clientDto) {
        Optional<Client> clientP=clientRepositorie.findByRut(clientDto.getRut());
        if(clientP.isPresent()){
            throw new IllegalArgumentException("Client with rut " + clientDto.getRut() + " already exists");
        }
        Client client = new Client();
        client.setRut(clientDto.getRut());
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmailC());
        client.setPhone(clientDto.getPhone());
        client.setStatus(Boolean.TRUE);
        clientRepositorie.save(client);
        return client;
    }

    //Read
    @Override
    public Client findByName(String name){
        return clientRepositorie.findByFirstName(name).orElse(null);
    }
    @Override
    public Client findById(Long id){
        return clientRepositorie.findById(id).orElse(null);
    }
    @Override
    public List<Client> findAll() {
        return clientRepositorie.findAll();
    }

    @Override
    public List<Client> findDelayedClient(){return clientRepositorie.findClientsDelayed();}


    //Update
    @Override
    public void updateCustomer(Client client, Long id){
        Client oldcustomer = clientRepositorie.findById(id).orElse(null);
        if(oldcustomer!=null){
            oldcustomer.setFirstName(client.getFirstName());
            oldcustomer.setLastName(client.getLastName());
            oldcustomer.setRut(client.getRut());
            oldcustomer.setEmail(client.getEmail());
            oldcustomer.setPhone(client.getPhone());
            clientRepositorie.save(oldcustomer);
        }else {
            throw new EntityNotFoundException("Client with ID " + id + " not found");
        }
    }

    @Override
    public void updateStatusCustomer(Long idClient){
        Client client = clientRepositorie.findById(idClient).orElse(null);
        if(client !=null){
            if(client.getStatus().equals(Boolean.TRUE)){
                client.setStatus(Boolean.FALSE);
            }
            else {
                client.setStatus(Boolean.TRUE);
            }
        }
        clientRepositorie.save(client);
    }

    //Delete
    @Override
    public void deleteCustomer(Client client){
        client.setStatus(Boolean.FALSE);
    }


    @Override
    @Transactional
    public void restrictClientsWithDelayedLoans() {
        List<Loan> delayedLoans = loanRepositorie.findActiveAndDelayedLoans();

        Set<Client> clientsToRestrict = delayedLoans.stream().map(Loan::getClient)
                .filter(client -> Boolean.TRUE.equals(client.getStatus()))
                .collect(Collectors.toSet());

        for (Client client : clientsToRestrict) {
            client.setStatus(false); // Cambiar a restrict
            clientRepositorie.save(client);
        }
    }

    @Override
    public List<String> searchRuts(String partialRut) {
        return clientRepositorie.findByRutContainingIgnoreCase(partialRut)
                .stream()
                .map(Client::getRut)
                .toList();
    }

}
