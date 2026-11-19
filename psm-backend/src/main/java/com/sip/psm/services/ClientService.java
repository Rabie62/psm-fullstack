package com.sip.psm.services;

import com.sip.psm.entities.Client;
import com.sip.psm.exceptions.ResourceNotFoundException;
import com.sip.psm.repositories.ClientRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "clients");

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Page<Client> searchAndPaginate(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (keyword == null || keyword.isBlank()) {
            return clientRepository.findAll(pageRequest);
        }
        return clientRepository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageRequest);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public Client update(Long id, Client clientRequest) {
        return clientRepository.findById(id).map(client -> {
            client.setNom(clientRequest.getNom());
            client.setTel(clientRequest.getTel());
            client.setEmail(clientRequest.getEmail());
            client.setAdresse(clientRequest.getAdresse());
            if (clientRequest.getPhoto() != null && !clientRequest.getPhoto().isBlank()) {
                client.setPhoto(clientRequest.getPhoto());
            }
            client.setActivite(clientRequest.getActivite());
            return clientRepository.save(client);
        }).orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
    }

    public Client uploadLogo(Long clientId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier envoyé est vide ou manquant.");
        }
        Client client = findById(clientId);
        Files.createDirectories(uploadDir);

        if (client.getPhoto() != null && !client.getPhoto().isBlank()) {
            try {
                Files.deleteIfExists(uploadDir.resolve(client.getPhoto()));
            } catch (Exception ignored) {}
        }

        String rawOriginalName = file.getOriginalFilename();
        String safeName = (rawOriginalName != null && !rawOriginalName.isBlank())
                ? Paths.get(rawOriginalName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_")
                : "logo.png";
        String filename = UUID.randomUUID() + "_" + safeName;
        Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        client.setPhoto(filename);
        return clientRepository.save(client);
    }

    public Resource loadLogo(Long clientId) {
        Client client = findById(clientId);
        if (client.getPhoto() == null || client.getPhoto().isBlank()) {
            throw new ResourceNotFoundException("Aucun logo enregistré pour le client id : " + clientId);
        }
        Path filePath = uploadDir.resolve(client.getPhoto()).normalize();
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Fichier logo introuvable sur le disque : " + client.getPhoto());
        }
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Le fichier logo ne peut pas être lu.");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Erreur de chemin de fichier : " + e.getMessage());
        }
    }

    public Client delete(Long id) {
        return clientRepository.findById(id).map(client -> {
            clientRepository.delete(client);
            return client;
        }).orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
    }
}
