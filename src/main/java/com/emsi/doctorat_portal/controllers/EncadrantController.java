package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.repositories.UserRepository;
import com.emsi.doctorat_portal.services.DoctoratService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/encadrant")
@RequiredArgsConstructor
public class EncadrantController {

    private final DoctoratService doctoratService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Récupération de l'encadrant via l'email de session
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Liste globale des doctorants suivis par cet encadrant
        List<Doctorant> mesDoctorants = doctoratService.getDoctorantsParEncadrant(currentUser.getId());

        // Liste spécifique des dossiers en attente d'avis (Module 2 du CdC)
        List<Doctorant> dossiersAValider = doctoratService.getDossiersEnAttenteAvis(currentUser.getId());

        model.addAttribute("doctorants", mesDoctorants);
        model.addAttribute("dossiersAValider", dossiersAValider);
        model.addAttribute("encadrantNom", currentUser.getNom() + " " + currentUser.getPrenom());

        return "encadrant/dashboard";
    }

    @PostMapping("/valider-inscription")
    public String validerInscription(@RequestParam Long id, @RequestParam boolean favorable) {
        // Met à jour le statut du dossier selon l'avis de l'encadrant
        doctoratService.validerDossierParDirecteur(id, favorable);
        return "redirect:/encadrant/dashboard";
    }

    @PostMapping("/proposer-jury")
    public String soumettreJury(@RequestParam Long doctorantId, @RequestParam String proposition) {
        // Enregistre la proposition de jury pour le module soutenance
        doctoratService.proposerJury(doctorantId, proposition);
        return "redirect:/encadrant/dashboard";
    }
}