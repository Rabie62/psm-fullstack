package com.sip.psm.repositories;

import com.sip.psm.entities.ETaskStatus;
import com.sip.psm.entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByProjetId(Long projetId);
    List<Tache> findByCollaborateurId(Long collaborateurId);
    long countByStatut(ETaskStatus statut);
}
