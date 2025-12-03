package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepositorie clientRepositorie;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClient_Success() {
        CreateClientDto dto = new CreateClientDto();
        dto.setRut("12.345.678-9");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmailC("john.doe@gmail.com");
        dto.setPhone("+56912345678");

        when(clientRepositorie.findByRut(dto.getRut())).thenReturn(Optional.empty());
        when(clientRepositorie.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client savedClient = clientService.save(dto);

        assertNotNull(savedClient);
        assertEquals("John", savedClient.getFirstName());
        assertEquals("12.345.678-9", savedClient.getRut());
        assertTrue(savedClient.getStatus());
        verify(clientRepositorie, times(1)).save(any(Client.class));
    }

    @Test
    void testSaveClient_AlreadyExists() {
        CreateClientDto dto = new CreateClientDto();
        dto.setRut("12.345.678-9");

        when(clientRepositorie.findByRut(dto.getRut())).thenReturn(Optional.of(new Client()));

        assertThrows(IllegalArgumentException.class, () -> clientService.save(dto));
        verify(clientRepositorie, never()).save(any(Client.class));
    }

    @Test
    void testUpdateCustomer_Success() {
        Long id = 1L;
        Client existingClient = new Client();
        existingClient.setIdClient(id);
        existingClient.setFirstName("OldName");

        Client updateData = new Client();
        updateData.setFirstName("NewName");
        updateData.setLastName("NewLast");
        updateData.setRut("11.111.111-1");
        updateData.setEmail("new@gmail.com");
        updateData.setPhone("99999999");

        when(clientRepositorie.findById(id)).thenReturn(Optional.of(existingClient));
        when(clientRepositorie.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clientService.updateCustomer(updateData, id);

        assertEquals("NewName", existingClient.getFirstName());
        assertEquals("NewLast", existingClient.getLastName());
        verify(clientRepositorie, times(1)).save(existingClient);
    }

    @Test
    void testUpdateStatusCustomer() {
        Long id = 1L;
        Client client = new Client();
        client.setIdClient(id);
        client.setStatus(true);

        when(clientRepositorie.findById(id)).thenReturn(Optional.of(client));
        when(clientRepositorie.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clientService.updateStatusCustomer(id);

        assertFalse(client.getStatus());
        verify(clientRepositorie, times(1)).save(client);
    }
}
