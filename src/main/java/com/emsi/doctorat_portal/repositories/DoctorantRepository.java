package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.enums.StatutDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorantRepository extends JpaRepository<Doctorant, Long> {

    // 1. Récupérer tous les doctorants d'un encadrant spécifique
    List<Doctorant> findByEncadrantId(Long encadrantId);

    // 2. Filtrer les doctorants d'un encadrant par statut (ex: EN_ATTENTE_DIRECTEUR)
    // Utile pour la section "Dossiers à valider" du dashboard encadrant
    List<Doctorant> findByEncadrantIdAndStatut(Long encadrantId, StatutDossier statut);

    // 3. Trouver un doctorant par l'email de son compte utilisateur
    // Très utile pour le Principal (sécurité) dans les contrôleurs
    Doctorant findByUserEmail(String email);

    // 4. Compter les doctorants par encadrant (pour les stats du dashboard)
    long countByEncadrantId(Long encadrantId);
}