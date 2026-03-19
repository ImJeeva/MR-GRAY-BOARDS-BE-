package com.invoice.invoice.service;

import com.invoice.invoice.entity.Client;
import com.invoice.invoice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }
    
    @Transactional
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }
    
    @Transactional
    public Client updateClient(Long id, Client client) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        existing.setName(client.getName());
        existing.setEmail(client.getEmail());
        existing.setPhone(client.getPhone());
        existing.setAddress(client.getAddress());
        existing.setCity(client.getCity());
        existing.setState(client.getState());
        existing.setZipCode(client.getZipCode());
        existing.setCountry(client.getCountry());
        return clientRepository.save(existing);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
    
    public List<Client> searchClients(String query) {
        return clientRepository.findByNameContainingIgnoreCase(query);
    }
}
