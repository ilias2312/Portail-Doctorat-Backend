package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.*;
import com.emsi.doctorat_portal.enums.Role;
import com.emsi.doctorat_portal.enums.StatutDossier;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.UserRepository;
import com.emsi.doctorat_portal.services.DoctoratService;
import com.emsi.doctorat_portal.services.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorantRepository doctorantRepository;
    private final UserRepository userRepository;
    private final DoctoratService doctoratService;
    private final SoutenanceService soutenanceService;
    private final PasswordEncoder passwordEncoder;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Doctorant> doctorants = doctorantRepository.findAll();

        long eligibles = doctorants.stream()
                .filter(d -> doctoratService.verifierEligibiliteSoutenance(d.getId()))
                .count();

        List<Doctorant> enAttenteAdmin = doctorants.stream()
                .filter(d -> d.getStatut() != null &&
                        d.getStatut() == StatutDossier.EN_ATTENTE_ADMIN)
                .toList();

        long enDerogation = doctorants.stream()
                .filter(d -> doctoratService.verifierStatutAnciennete(d.getId())
                        .equals("DÉROGATION REQUISE"))
                .count();

        List<Doctorant> exclus = doctorants.stream()
                .filter(d -> doctoratService.verifierStatutAnciennete(d.getId())
                        .equals("EXCLUS"))
                .toList();

        model.addAttribute("ds", doctoratService);
        model.addAttribute("doctorants", doctorants);
        model.addAttribute("total", doctorants.size());
        model.addAttribute("eligibles", eligibles);
        model.addAttribute("enAttenteAdmin", enAttenteAdmin);
        model.addAttribute("enDerogation", enDerogation);
        model.addAttribute("exclus", exclus);

        return "admin/dashboard";
    }

    // ================= INSCRIPTION =================
    @GetMapping("/inscription")
    public String showInscriptionForm(Model model) {
        model.addAttribute("doctorant", new Doctorant());
        model.addAttribute("encadrants", userRepository.findByRole(Role.ENCADRANT));
        return "admin/inscription-doctorant";
    }

    @PostMapping("/inscription/save")
    public String saveDoctorant(@ModelAttribute Doctorant doctorant) {
        if (doctorant.getId() == null) {
            User user = new User();
            user.setNom(doctorant.getNom());
            user.setPrenom(doctorant.getPrenom());
            user.setEmail(doctorant.getEmail());
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(Role.DOCTORANT);
            doctorant.setUser(user);
        }
        doctorantRepository.save(doctorant);
        return "redirect:/admin/dashboard";
    }

    // ================= EDIT =================
    @GetMapping("/doctorant/edit/{id}")
    public String editDoctorant(@PathVariable Long id, Model model) {
        Doctorant doctorant = doctorantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));
        model.addAttribute("doctorant", doctorant);
        model.addAttribute("encadrants", userRepository.findByRole(Role.ENCADRANT));
        return "admin/inscription-doctorant";
    }

    @PostMapping("/doctorant/update")
    public String updateDoctorant(@ModelAttribute Doctorant doctorant) {
        Doctorant existing = doctorantRepository.findById(doctorant.getId())
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));
        existing.setNom(doctorant.getNom());
        existing.setPrenom(doctorant.getPrenom());
        existing.setSujetThese(doctorant.getSujetThese());
        existing.setTotalHeuresFormation(doctorant.getTotalHeuresFormation());
        existing.setStatut(doctorant.getStatut());
        existing.setEncadrant(doctorant.getEncadrant());
        doctorantRepository.save(existing);
        return "redirect:/admin/dashboard";
    }

    // ================= DELETE =================
    @GetMapping("/doctorant/delete/{id}")
    public String deleteDoctorant(@PathVariable Long id) {
        doctorantRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // ================= VALIDATION ADMIN DOSSIERS =================
    @GetMapping("/dossier/valider/{id}")
    public String validerDossierAdmin(@PathVariable Long id) {
        Doctorant d = doctorantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));
        d.setStatut(StatutDossier.VALIDE_ADMIN);
        doctorantRepository.save(d);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dossier/rejeter/{id}")
    public String rejeterDossierAdmin(@PathVariable Long id) {
        Doctorant d = doctorantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));
        d.setStatut(StatutDossier.REJETE);
        doctorantRepository.save(d);
        return "redirect:/admin/dashboard";
    }

    // ================= SOUTENANCES =================
    @GetMapping("/soutenances")
    public String listeSoutenances(Model model) {
        model.addAttribute("soutenances", soutenanceService.getAll());
        return "admin/soutenances";
    }

    @GetMapping("/soutenance/valider/{id}")
    public String valider(@PathVariable Long id) {
        soutenanceService.valider(id);
        return "redirect:/admin/soutenances";
    }

    @GetMapping("/soutenance/refuser/{id}")
    public String refuser(@PathVariable Long id) {
        soutenanceService.refuser(id);
        return "redirect:/admin/soutenances";
    }

    @PostMapping("/soutenance/planifier/{id}")
    public String planifier(@PathVariable Long id,
                            @RequestParam String date,
                            @RequestParam String salle) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            Soutenance s = new Soutenance();
            s.setDateSoutenance(dateTime);
            s.setSalle(salle);
            soutenanceService.planifier(id, s);
        } catch (Exception e) {
            System.out.println("Erreur planification soutenance: " + e.getMessage());
        }
        return "redirect:/admin/soutenances";
    }
}