package com.emsi.doctorat_portal.entities;

import com.emsi.doctorat_portal.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctorant doctorant;

    // Ajout pour l'encadrant : liste des doctorants qu'il suit
    @OneToMany(mappedBy = "encadrant")
    private List<Doctorant> mesDoctorants;
}