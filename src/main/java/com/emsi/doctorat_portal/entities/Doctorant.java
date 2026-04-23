package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.StatutDossier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctorants")
@PrimaryKeyJoinColumn(name = "id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Doctorant extends User {

    private String sujetThese;

    private LocalDate dateInscriptionInitiale;

    // Correction : Ajout du champ manquant pour la planification administrative
    private LocalDate dateSoutenance;

    private int totalHeuresFormation;

    @Enumerated(EnumType.STRING)
    private StatutDossier statut;

    @ManyToOne
    @JoinColumn(name = "directeur_id")
    private User directeurThese;

    @OneToMany(mappedBy = "doctorant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Publication> publications = new ArrayList<>();

    /**
     * Initialisation sécurisée de la liste des publications
     * pour éviter les NullPointerException lors des tests.
     */
    public List<Publication> getPublications() {
        if (this.publications == null) {
            this.publications = new ArrayList<>();
        }
        return this.publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
}