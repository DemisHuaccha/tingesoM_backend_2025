package com.example.tingesoM.Controller.UserController;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import com.example.tingesoM.Service.ServiceImpl.LoanServiceImpl;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins="*")
public class ClientController {
    @Autowired
    private ClientServiceImpl clientServiceImpl;
    @Autowired
    private CardexServiceImpl cardexServiceImpl;
    @Autowired
    private LoanServiceImpl loanServiceImpl;

    @PostMapping("/createClient")
    public ResponseEntity<Client> createClient(@RequestBody CreateClientDto clientDto) {
        Client client=clientServiceImpl.save(clientDto);
        cardexServiceImpl.saveCardexClient(clientDto,client);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> tools = clientServiceImpl.findAll();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientServiceImpl.findById(id));
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        return ResponseEntity.ok(clientServiceImpl.findByName(name));
    }

    @PutMapping("/updateStatus/{idClient}")
    public ResponseEntity<Client> updateStatusClient(@PathVariable Long idClient) {
        clientServiceImpl.updateStatusCustomer(idClient);
        return ResponseEntity.ok(clientServiceImpl.findById(idClient));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Client> updateClient(@RequestBody Client client, @PathVariable Long id) {
        clientServiceImpl.updateCustomer(client, id);
        return ResponseEntity.ok(clientServiceImpl.findById(id));
    }

    @GetMapping("/getDelayedClients")
    public ResponseEntity<List<Client>> getDelayedClients(){
        return ResponseEntity.ok(clientServiceImpl.findDelayedClient());
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchRuts(@RequestParam String rut) {
        List<String> ruts = clientServiceImpl.searchRuts(rut);
        return ResponseEntity.ok(ruts);
    }

}
