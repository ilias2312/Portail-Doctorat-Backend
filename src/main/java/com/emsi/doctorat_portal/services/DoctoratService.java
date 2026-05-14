package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Publication;
import java.util.List;

public interface DoctoratService {
    // Méthodes pour le Dashboard et l'Inscription
    List<Doctorant> getAllDoctorants();
    void saveDoctorant(Doctorant doctorant);

    // Méthodes spécifiques à l'Encadrant (Module 2 & 3 du CdC)
    List<Doctorant> getDoctorantsParEncadrant(Long encadrantId);
    List<Doctorant> getDossiersEnAttenteAvis(Long encadrantId);
    void validerDossierParDirecteur(Long doctorantId, boolean estFavorable);
    void proposerJury(Long doctorantId, String proposition);

    // Autres méthodes métier
    void ajouterPublication(Long doctorantId, Publication publication);
    boolean verifierEligibiliteSoutenance(Long doctorantId);
    String verifierStatutAnciennete(Long doctorantId);
    Doctorant getDoctorantByEmail(String email);
}