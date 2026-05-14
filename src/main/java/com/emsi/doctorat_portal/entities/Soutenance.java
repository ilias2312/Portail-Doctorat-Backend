package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.StatutSoutenance;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "soutenances")
@Data @NoArgsConstructor @AllArgsConstructor
public class Soutenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_soutenance")
    private LocalDateTime dateSoutenance;

    private String salle;

    // Champ requis pour corriger l'erreur dans le service[cite: 1]
    @Column(columnDefinition = "TEXT")
    private String propositionJury;

    @Enumerated(EnumType.STRING)
    private StatutSoutenance statut;

    @OneToOne
    @JoinColumn(name = "doctorant_id")
    private Doctorant doctorant;
}