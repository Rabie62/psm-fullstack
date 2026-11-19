package com.sip.psm.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "libelle", length = 30, unique = true, nullable = false)
    private ERole libelle;

    public Role() {
    }

    public Role(ERole libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getLibelle() {
        return libelle;
    }

    public void setLibelle(ERole libelle) {
        this.libelle = libelle;
    }
}
