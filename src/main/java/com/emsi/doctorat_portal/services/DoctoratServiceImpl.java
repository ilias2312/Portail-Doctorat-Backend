package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Publication;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctoratServiceImpl implements DoctoratService {

    private final DoctorantRepository doctorantRepository;
    private final PublicationRepository publicationRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<Doctorant> getAllDoctorants() {
        List<Doctorant> res = doctorantRepository.findAll();
        return res != null ? res : new ArrayList<>();
    }

    @Override
    public void saveDoctorant(Doctorant doctorant) {
        if (doctorant.getPassword() == null || doctorant.getPassword().isEmpty()) {
            doctorant.setPassword("123456");
        }
        if (doctorant.getDateInscriptionInitiale() == null) {
            doctorant.setDateInscriptionInitiale(LocalDate.now());
        }
        doctorantRepository.save(doctorant);
    }

    @Override
    public void ajouterPublication(Long doctorantId, Publication publication) {
        Doctorant doc = doctorantRepository.findById(doctorantId)
                .orElseThrow(() -> new RuntimeException("Doctorant non trouvé"));
        publication.setDoctorant(doc);
        publicationRepository.save(publication);
    }

    @Override
    public boolean verifierEligibiliteSoutenance(Long doctorantId) {
        Doctorant doc = doctorantRepository.findById(doctorantId).orElse(null);
        if (doc == null || doc.getPublications() == null) return false;

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
        if (doc == null || doc.getDateInscriptionInitiale() == null) return "STATUT INCONNU";

        long annees = ChronoUnit.YEARS.between(doc.getDateInscriptionInitiale(), LocalDate.now());
        if (annees >= 6) return "EXCLUS (Limite dépassée)";
        if (annees >= 3) return "DÉROGATION REQUISE";
        return "CURSUS NORMAL";
    }

}