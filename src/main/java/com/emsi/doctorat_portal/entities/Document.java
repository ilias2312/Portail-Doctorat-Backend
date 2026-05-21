package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;           // nom original du fichier
    private String cheminFichier; // chemin stocké sur disque
    private String contentType;   // ex: application/pdf

    @Enumerated(EnumType.STRING)
    private TypeDocument type;    // PIECE_JUSTIFICATIVE ou MANUSCRIT

    private LocalDateTime dateDepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorant_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Doctorant doctorant;
}