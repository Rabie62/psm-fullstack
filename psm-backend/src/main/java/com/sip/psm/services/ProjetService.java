package com.sip.psm.services;

import com.sip.psm.entities.Client;
import com.sip.psm.entities.EProjectStatus;
import com.sip.psm.entities.Projet;
import com.sip.psm.exceptions.ResourceNotFoundException;
import com.sip.psm.repositories.ClientRepository;
import com.sip.psm.repositories.ProjetRepository;
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
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final ClientRepository clientRepository;
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "projets");

    public ProjetService(ProjetRepository projetRepository, ClientRepository clientRepository) {
        this.projetRepository = projetRepository;
        this.clientRepository = clientRepository;
    }

    public List<Projet> findAll() {
        return projetRepository.findAll();
    }

    public Page<Projet> searchAndPaginate(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (keyword == null || keyword.isBlank()) {
            return projetRepository.findAll(pageRequest);
        }
        return projetRepository.findByLibelleContainingIgnoreCase(keyword, pageRequest);
    }

    public Projet findById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'id : " + id));
    }

    public List<Projet> findByClientId(Long clientId) {
        return projetRepository.findByClientId(clientId);
    }

    public Projet create(Long clientId, Projet projet) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + clientId));
        if (projet.getStatut() == null) {
            projet.setStatut(EProjectStatus.EN_COURS);
        }
        projet.setClient(client);
        return projetRepository.save(projet);
    }

    public Projet update(Long clientId, Long projetId, Projet projetRequest) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + clientId));

        return projetRepository.findById(projetId).map(projet -> {
            projet.setLibelle(projetRequest.getLibelle());
            projet.setBudget(projetRequest.getBudget());
            projet.setDuree(projetRequest.getDuree());
            projet.setDescription(projetRequest.getDescription());
            if (projetRequest.getStatut() != null) {
                projet.setStatut(projetRequest.getStatut());
            }
            if (projetRequest.getDocumentCahierCharges() != null && !projetRequest.getDocumentCahierCharges().isBlank()) {
                projet.setDocumentCahierCharges(projetRequest.getDocumentCahierCharges());
            }
            projet.setClient(client);
            return projetRepository.save(projet);
        }).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'id : " + projetId));
    }

    public Projet uploadCahierCharges(Long projetId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le document envoyé est vide ou manquant.");
        }
        Projet projet = findById(projetId);
        Files.createDirectories(uploadDir);

        if (projet.getDocumentCahierCharges() != null && !projet.getDocumentCahierCharges().isBlank()) {
            try {
                Files.deleteIfExists(uploadDir.resolve(projet.getDocumentCahierCharges()));
            } catch (Exception ignored) {}
        }

        String rawOriginalName = file.getOriginalFilename();
        String safeName = (rawOriginalName != null && !rawOriginalName.isBlank())
                ? Paths.get(rawOriginalName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_")
                : "cahier_des_charges.pdf";
        String filename = UUID.randomUUID() + "_" + safeName;
        Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        projet.setDocumentCahierCharges(filename);
        return projetRepository.save(projet);
    }

    public Resource loadCahierCharges(Long projetId) {
        Projet projet = findById(projetId);
        if (projet.getDocumentCahierCharges() == null || projet.getDocumentCahierCharges().isBlank()) {
            throw new ResourceNotFoundException("Aucun document de cahier des charges pour le projet id : " + projetId);
        }
        Path filePath = uploadDir.resolve(projet.getDocumentCahierCharges()).normalize();
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Fichier document introuvable sur le disque : " + projet.getDocumentCahierCharges());
        }
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Le document ne peut pas être lu.");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Erreur de chemin de fichier : " + e.getMessage());
        }
    }

    public String exportProjetsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID;Libelle;Client;Budget_TND;Duree;Statut;Cree_Par\n");
        for (Projet p : projetRepository.findAll()) {
            sb.append(p.getId()).append(";")
              .append(p.getLibelle().replace(";", ",")).append(";")
              .append(p.getClient() != null ? p.getClient().getNom().replace(";", ",") : "").append(";")
              .append(p.getBudget()).append(";")
              .append(p.getDuree()).append(";")
              .append(p.getStatut() != null ? p.getStatut().name() : "EN_COURS").append(";")
              .append(p.getCreatedBy() != null ? p.getCreatedBy() : "SYSTEM").append("\n");
        }
        return sb.toString();
    }

    public Projet delete(Long projetId) {
        return projetRepository.findById(projetId).map(projet -> {
            projetRepository.delete(projet);
            return projet;
        }).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'id : " + projetId));
    }
}
