package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.StatutDossier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "doctorants")
@Data @NoArgsConstructor @AllArgsConstructor
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private User encadrant;

    @OneToMany(mappedBy = "doctorant", cascade = CascadeType.ALL)
    private List<Publication> publications;

    // Cette ligne corrige l'erreur rouge getSoutenance() dans le service
    @OneToOne(mappedBy = "doctorant", cascade = CascadeType.ALL)
    private Soutenance soutenance;
}