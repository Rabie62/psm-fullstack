package com.sip.psm.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/**
 * Entité Tache (Recommandation 2 : Gestion des Tâches Kanban et Affectation des Collaborateurs aux Projets).
 */
@Entity
@Table(name = "taches")
public class Tache extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private ETaskStatus statut = ETaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false)
    private ETaskPriority priorite = ETaskPriority.MEDIUM;

    @Min(0)
    @Max(100)
    @Column(name = "progression")
    private int progression = 0;

    @Column(name = "deadline")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_projet", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Projet projet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_collaborateur")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User collaborateur;

    public Tache() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ETaskStatus getStatut() { return statut; }
    public void setStatut(ETaskStatus statut) { this.statut = statut; }
    public ETaskPriority getPriorite() { return priorite; }
    public void setPriorite(ETaskPriority priorite) { this.priorite = priorite; }
    public int getProgression() { return progression; }
    public void setProgression(int progression) { this.progression = progression; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public User getCollaborateur() { return collaborateur; }
    public void setCollaborateur(User collaborateur) { this.collaborateur = collaborateur; }
}
