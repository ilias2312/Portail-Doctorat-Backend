package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Publication;
import com.emsi.doctorat_portal.entities.Soutenance;
import com.emsi.doctorat_portal.enums.StatutDossier;
import com.emsi.doctorat_portal.enums.StatutSoutenance;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.PublicationRepository;
import com.emsi.doctorat_portal.repositories.SoutenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctoratServiceImpl implements DoctoratService {

    private final DoctorantRepository doctorantRepository;
    private final PublicationRepository publicationRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Doctorant> getAllDoctorants() {
        return doctorantRepository.findAll();
    }

    @Override
    public List<Doctorant> getDoctorantsParEncadrant(Long encadrantId) {
        return doctorantRepository.findByEncadrantId(encadrantId);
    }

    @Override
    public List<Doctorant> getDossiersEnAttenteAvis(Long encadrantId) {
        // Module 2 : Inscription - Filtrage par statut spécifique
        return doctorantRepository.findByEncadrantIdAndStatut(encadrantId, StatutDossier.EN_ATTENTE_DIRECTEUR);
    }

    @Override
    public void validerDossierParDirecteur(Long doctorantId, boolean estFavorable) {
        Doctorant doctorant = doctorantRepository.findById(doctorantId)
                .orElseThrow(() -> new RuntimeException("Doctorant non trouvé"));

        if (estFavorable) {
            // Transfert vers l'administration après avis directeur[cite: 1]
            doctorant.setStatut(StatutDossier.EN_ATTENTE_ADMIN);
        } else {
            doctorant.setStatut(StatutDossier.REJETE);
        }
        doctorantRepository.save(doctorant);
    }

    @Override
    public void proposerJury(Long doctorantId, String proposition) {
        Doctorant doctorant = doctorantRepository.findById(doctorantId)
                .orElseThrow(() -> new RuntimeException("Doctorant non trouvé"));

        Soutenance soutenance = doctorant.getSoutenance();

        if (soutenance == null) {
            soutenance = new Soutenance();
            soutenance.setDoctorant(doctorant);
        }

        // Module 3 : Soutenance - Proposition des membres du jury[cite: 1]
        soutenance.setPropositionJury(proposition);
        soutenance.setStatut(StatutSoutenance.EN_ATTENTE_VALIDATION_ADMIN);

        soutenanceRepository.save(soutenance);
    }

    @Override
    public void saveDoctorant(Doctorant doctorant) {
        if (doctorant.getUser() != null && (doctorant.getUser().getPassword() == null || doctorant.getUser().getPassword().isEmpty())) {
            doctorant.getUser().setPassword(passwordEncoder.encode("123456"));
        }
        if (doctorant.getDateInscriptionInitiale() == null) {
            doctorant.setDateInscriptionInitiale(LocalDate.now());
        }
        doctorantRepository.save(doctorant);
    }

    @Override
    public void ajouterPublication(Long doctorantId, Publication publication) {
        Doctorant doc = doctorantRepository.findById(doctorantId).orElseThrow();
        publication.setDoctorant(doc);
        publicationRepository.save(publication);
    }

    @Override
    public boolean verifierEligibiliteSoutenance(Long doctorantId) {
        Doctorant doc = doctorantRepository.findById(doctorantId).orElse(null);
        if (doc == null) return false;

        // Validation des critères : 2 articles indexés + 2 conférences + 200h formation[cite: 1]
        long q1q2Count = doc.getPublications().stream()
                .filter(p -> "JOURNAL".equalsIgnoreCase(p.getType()) &&
                        (p.getRang() != null && (p.getRang().equalsIgnoreCase("Q1") || p.getRang().equalsIgnoreCase("Q2"))))
                .count();

        long confCount = doc.getPublications().stream()
                .filter(p -> "CONFERENCE".equalsIgnoreCase(p.getType()))
                .count();

        return q1q2Count >= 2 && confCount >= 2 && doc.getTotalHeuresFormation() >= 200;
    }

    @Override
    public String verifierStatutAnciennete(Long doctorantId) {
        Doctorant doc = doctorantRepository.findById(doctorantId).orElse(null);
        if (doc == null || doc.getDateInscriptionInitiale() == null) return "INCONNU";

        long annees = ChronoUnit.YEARS.between(doc.getDateInscriptionInitiale(), LocalDate.now());
        if (annees >= 6) return "EXCLUS"; // Délai de rigueur dépassé[cite: 1]
        if (annees >= 3) return "DÉROGATION REQUISE"; // Fin de cycle normal[cite: 1]
        return "CURSUS NORMAL";
    }

    @Override
    public Doctorant getDoctorantByEmail(String email) {
        return doctorantRepository.findByUserEmail(email);
    }
}