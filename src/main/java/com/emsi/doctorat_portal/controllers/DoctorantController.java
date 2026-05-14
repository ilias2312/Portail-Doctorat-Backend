package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Publication;
import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.repositories.UserRepository;
import com.emsi.doctorat_portal.services.DoctoratService;
import com.emsi.doctorat_portal.services.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/doctorant")
@RequiredArgsConstructor
public class DoctorantController {

    private final DoctoratService doctoratService;
    private final UserRepository userRepository;
    private final SoutenanceService soutenanceService;

    // ================= DASHBOARD =================
    @GetMapping({"/dashboard", "/index"})
    public String doctorantDashboard(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Doctorant d = user.getDoctorant();
        model.addAttribute("d", d);
        return "doctorant/dashboard";
    }

    // ================= PUBLICATION =================
    @GetMapping("/publication/add")
    public String showAddPublicationForm(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        model.addAttribute("publication", new Publication());
        model.addAttribute("doctorId", user.getDoctorant().getId());
        return "doctorant/add-publication";
    }

    @PostMapping("/publication/save")
    public String savePublication(@ModelAttribute Publication pub, @RequestParam Long doctorId) {
        doctoratService.ajouterPublication(doctorId, pub);
        return "redirect:/doctorant/dashboard";
    }

    // ================= SOUTENANCE (CORRIGÉ) =================
    @GetMapping("/soutenance/demander") // Changé en GET pour correspondre au lien HTML
    public String demanderSoutenance(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Doctorant d = user.getDoctorant();

        // Logique métier : On déclenche la demande via le service
        soutenanceService.demanderSoutenance(d.getId());

        System.out.println("🔥 Demande de soutenance enregistrée pour : " + d.getUser().getNom());

        return "redirect:/doctorant/dashboard";
    }
}