package com.sip.psm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Column(name = "tel", nullable = false)
    private String tel;

    @NotBlank(message = "L'email du client est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column(name = "adresse", nullable = false)
    private String adresse;

    @Column(name = "photo")
    private String photo;

    @Column(name = "activite")
    private String activite;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Projet> projets = new ArrayList<>();

    public Client() {
    }

    public Client(String nom, String tel, String email, String adresse, String photo, String activite) {
        this.nom = nom;
        this.tel = tel;
        this.email = email;
        this.adresse = adresse;
        this.photo = photo;
        this.activite = activite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getActivite() { return activite; }
    public void setActivite(String activite) { this.activite = activite; }
    public List<Projet> getProjets() { return projets; }
    public void setProjets(List<Projet> projets) { this.projets = projets; }
}
