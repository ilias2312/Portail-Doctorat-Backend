package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Publication;
import com.emsi.doctorat_portal.services.DoctoratService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DoctorantController {

    private final DoctoratService doctoratService;

    // 1. Point d'entrée après la connexion pour le rôle DOCTORANT
    @GetMapping("/doctorant/index")
    public String doctorantIndex() {
        return "doctorant/index"; // Utilise le fichier templates/doctorant/index.html
    }

    // 2. Affiche le formulaire pour ajouter une publication
    @GetMapping("/doctorant/publication/add/{id}")
    public String showAddPublicationForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("publication", new Publication());
        model.addAttribute("doctorId", id);
        // Correction : on utilise le dossier 'doctorant' vu dans ton arborescence
        return "doctorant/add-publication";
    }

    // 3. Enregistre la publication
    @PostMapping("/doctorant/publication/save")
    public String savePublication(@ModelAttribute("publication") Publication pub,
                                  @RequestParam("doctorId") Long doctorId) {
        doctoratService.ajouterPublication(doctorId, pub);

        // Attention : Si le doctorant n'a pas accès à /admin,
        // redirige-le plutôt vers son propre index ou une page de succès.
        return "redirect:/doctorant/index";
    }
}