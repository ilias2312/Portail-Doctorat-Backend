package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Publication;
import java.util.List;

public interface DoctoratService {
    // Méthodes pour le Dashboard et l'Inscription
    List<Doctorant> getAllDoctorants();
    void saveDoctorant(Doctorant doctorant);

    // Autres méthodes métier
    void ajouterPublication(Long doctorantId, Publication publication);
    boolean verifierEligibiliteSoutenance(Long doctorantId);
    String verifierStatutAnciennete(Long doctorantId);
}