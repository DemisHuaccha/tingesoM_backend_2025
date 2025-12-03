package com.example.tingesoM.Repositorie.UserRepositorie;

import com.example.tingesoM.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepositorie extends JpaRepository<Client,Long> {
    Optional<Client> findByFirstName(String firstName);

    Optional<Client> findByRut(String rut);

    List<Client> findByRutContainingIgnoreCase(String rut);

    @Query("SELECT DISTINCT l.client FROM Loan l WHERE l.loanStatus = true AND l.returnDate < CURRENT_DATE")
    List<Client> findClientsDelayed();

}
