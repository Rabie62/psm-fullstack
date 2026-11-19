package com.sip.psm.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "projets")
public class Projet extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé du projet est obligatoire")
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @NotNull(message = "Le budget est obligatoire")
    @Min(value = 0, message = "Le budget doit être positif")
    @Column(name = "budget", nullable = false)
    private Double budget;

    @NotBlank(message = "La durée est obligatoire")
    @Column(name = "duree", nullable = false)
    private String duree;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 30)
    private EProjectStatus statut = EProjectStatus.EN_COURS;

    @Column(name = "document_cahier_charges")
    private String documentCahierCharges;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client;

    public Projet() {
    }

    public Projet(String libelle, Double budget, String duree, String description, EProjectStatus statut, Client client) {
        this.libelle = libelle;
        this.budget = budget;
        this.duree = duree;
        this.description = description;
        this.statut = statut != null ? statut : EProjectStatus.EN_COURS;
        this.client = client;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public EProjectStatus getStatut() { return statut; }
    public void setStatut(EProjectStatus statut) { this.statut = statut; }
    public String getDocumentCahierCharges() { return documentCahierCharges; }
    public void setDocumentCahierCharges(String documentCahierCharges) { this.documentCahierCharges = documentCahierCharges; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
