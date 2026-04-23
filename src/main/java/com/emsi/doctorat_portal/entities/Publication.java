package com.emsi.doctorat_portal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Publication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String type; // ex: "JOURNAL" ou "CONFERENCE"
    private String rang; // ex: "Q1", "Q2"
    private String lienPdf;

    @ManyToOne
    @JoinColumn(name = "doctorant_id")
    private Doctorant doctorant;
}