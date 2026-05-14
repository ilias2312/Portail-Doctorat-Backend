package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.*;
import com.emsi.doctorat_portal.enums.Role;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
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
    private final DoctoratService doctoratService;
    private final SoutenanceService soutenanceService;
    private final PasswordEncoder passwordEncoder;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<Doctorant> doctorants = doctorantRepository.findAll();

        model.addAttribute("ds", doctoratService);
        model.addAttribute("doctorants", doctorants);
        model.addAttribute("total", doctorants.size());

        return "admin/dashboard";
    }

    // ================= INSCRIPTION =================
    @GetMapping("/inscription")
    public String showInscriptionForm(Model model) {
        model.addAttribute("doctorant", new Doctorant());
        return "admin/inscription-doctorant";
    }

    @PostMapping("/inscription/save")
    public String saveDoctorant(@ModelAttribute Doctorant doctorant) {

        if (doctorant.getId() == null) {

            User user = new User();
            user.setNom(doctorant.getNom());
            user.setPrenom(doctorant.getPrenom());
            user.setEmail(doctorant.getEmail());
            user.setPassword(passwordEncoder.encode("1234"));
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

        doctorantRepository.save(existing);
        return "redirect:/admin/dashboard";
    }

    // ================= DELETE =================
    @GetMapping("/doctorant/delete/{id}")
    public String deleteDoctorant(@PathVariable Long id) {
        doctorantRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // =====================================================
    // 🥇 MODULE SOUTENANCE (CORRIGÉ PROPRE)
    // =====================================================

    // LISTE
    @GetMapping("/soutenances")
    public String listeSoutenances(Model model) {
        model.addAttribute("soutenances", soutenanceService.getAll());
        return "admin/soutenances";
    }

    // VALIDER
    @GetMapping("/soutenance/valider/{id}")
    public String valider(@PathVariable Long id) {
        soutenanceService.valider(id);
        return "redirect:/admin/soutenances";
    }

    // REFUSER
    @GetMapping("/soutenance/refuser/{id}")
    public String refuser(@PathVariable Long id) {
        soutenanceService.refuser(id);
        return "redirect:/admin/soutenances";
    }

    // PLANIFIER (CORRIGÉ + SAFE)
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