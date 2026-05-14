package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Soutenance;
import com.emsi.doctorat_portal.enums.StatutSoutenance;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.SoutenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SoutenanceService {

    @Autowired
    private DoctorantRepository doctorantRepository;

    @Autowired
    private SoutenanceRepository soutenanceRepository;

    // ================= ELIGIBILITE =================
    public boolean isEligible(Long id) {
        Doctorant d = doctorantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));

        return d.getTotalHeuresFormation() >= 200
                && d.getPublications() != null
                && d.getPublications().size() >= 2;
    }

    // ================= DEMANDE SOUTENANCE =================
    public void demanderSoutenance(Long id) {

        Doctorant d = doctorantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));

        // ✔ vérification conditions
        if (!isEligible(id)) {
            throw new RuntimeException("Conditions non remplies");
        }

        // ✔ éviter double demande
        Optional<Soutenance> exist = soutenanceRepository.findByDoctorant(d);

        if (exist.isPresent()) {
            return; // pas de crash, juste ignore
        }

        // ✔ création demande
        Soutenance s = new Soutenance();
        s.setDoctorant(d);
        s.setStatut(StatutSoutenance.DEMANDEE);

        soutenanceRepository.save(s);
    }

    // ================= ADMIN ACTIONS =================

    public void valider(Long id) {
        Soutenance s = soutenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soutenance introuvable"));

        s.setStatut(StatutSoutenance.VALIDE);

        soutenanceRepository.save(s);
    }

    public void refuser(Long id) {
        Soutenance s = soutenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soutenance introuvable"));

        s.setStatut(StatutSoutenance.REFUSEE);

        soutenanceRepository.save(s);
    }

    // ================= PLANIFICATION =================
    public void planifier(Long id, Soutenance request) {

        Soutenance s = soutenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soutenance introuvable"));

        s.setDateSoutenance(request.getDateSoutenance());
        s.setSalle(request.getSalle());
        s.setStatut(StatutSoutenance.PLANIFIEE);

        soutenanceRepository.save(s);
    }

    // ================= LISTE ADMIN =================
    public List<Soutenance> getAll() {
        return soutenanceRepository.findAll();
    }
}