package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.StatutDossier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "doctorants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Doctorant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String sujetThese;
    private int totalHeuresFormation;

    private LocalDate dateInscriptionInitiale;

    @Enumerated(EnumType.STRING)
    private StatutDossier statut;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadrant_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User encadrant;

    @OneToMany(mappedBy = "doctorant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Publication> publications;

    // ✅ Added Document relationship
    @OneToMany(mappedBy = "doctorant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Document> documents;

    @OneToOne(mappedBy = "doctorant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Soutenance soutenance;
}