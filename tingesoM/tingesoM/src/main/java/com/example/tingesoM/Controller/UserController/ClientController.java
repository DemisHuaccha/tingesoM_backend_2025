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
@RequestMapping("/api/customer")
@CrossOrigin(origins="http://Localhost:8070")
public class ClientController {
    @Autowired
    private ClientServiceImpl clientServiceImpl;
    @Autowired
    private CardexServiceImpl cardexServiceImpl;
    @Autowired
    private LoanServiceImpl loanServiceImpl;

    @PostMapping("/createCustomer")
    public ResponseEntity<Client> createCustomer(@RequestBody CreateClientDto clientDto) {
        Client client=clientServiceImpl.save(clientDto);
        cardexServiceImpl.saveCardexClient(clientDto,client);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Client>> getAllCustomers() {
        List<Client> tools = clientServiceImpl.findAll();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<Client> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(clientServiceImpl.findById(id));
    }

    @GetMapping("/getCustomer/{name}")
    public ResponseEntity<Client> getCustomerByName(@PathVariable String name) {
        return ResponseEntity.ok(clientServiceImpl.findByName(name));
    }

    @PutMapping("/updateStatus/{idCustomer}")
    public ResponseEntity<Client> updateStatusCustomer(@PathVariable Long idCustomer) {
        clientServiceImpl.updateStatusCustomer(idCustomer);
        return ResponseEntity.ok(clientServiceImpl.findById(idCustomer));
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
