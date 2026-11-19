package com.sip.psm.repositories;

import com.sip.psm.entities.EProjectStatus;
import com.sip.psm.entities.Projet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {
    List<Projet> findByClientId(Long clientId);
    Page<Projet> findByLibelleContainingIgnoreCase(String libelle, Pageable pageable);
    long countByStatut(EProjectStatus statut);

    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM Projet p")
    Double sumTotalBudget();
}
