package com.sip.psm.services;

import com.sip.psm.entities.ETaskStatus;
import com.sip.psm.entities.Projet;
import com.sip.psm.entities.Tache;
import com.sip.psm.entities.User;
import com.sip.psm.exceptions.ResourceNotFoundException;
import com.sip.psm.repositories.ProjetRepository;
import com.sip.psm.repositories.TacheRepository;
import com.sip.psm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TacheService {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;

    public TacheService(TacheRepository tacheRepository,
                        ProjetRepository projetRepository,
                        UserRepository userRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.userRepository = userRepository;
    }

    public List<Tache> findAll() {
        return tacheRepository.findAll();
    }

    public List<Tache> findByProjetId(Long projetId) {
        return tacheRepository.findByProjetId(projetId);
    }

    public Tache create(Long projetId, Long collaborateurId, Tache tache) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable : " + projetId));
        tache.setProjet(projet);

        if (collaborateurId != null) {
            User collab = userRepository.findById(collaborateurId)
                    .orElseThrow(() -> new ResourceNotFoundException("Collaborateur introuvable : " + collaborateurId));
            tache.setCollaborateur(collab);
        }
        return tacheRepository.save(tache);
    }

    public Tache updateStatusAndProgress(Long tacheId, ETaskStatus statut, int progression) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable : " + tacheId));
        tache.setStatut(statut);
        tache.setProgression(statut == ETaskStatus.DONE ? 100 : progression);
        return tacheRepository.save(tache);
    }

    public void delete(Long tacheId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable : " + tacheId));
        tacheRepository.delete(tache);
    }
}
