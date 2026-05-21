package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.Soutenance;
import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.enums.StatutSoutenance;
import com.emsi.doctorat_portal.repositories.SoutenanceRepository;
import com.emsi.doctorat_portal.repositories.UserRepository;
import com.emsi.doctorat_portal.services.DoctoratService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/encadrant")
@RequiredArgsConstructor
public class EncadrantController {

    private final DoctoratService doctoratService;
    private final UserRepository userRepository;
    private final SoutenanceRepository soutenanceRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Doctorant> mesDoctorants = doctoratService.getDoctorantsParEncadrant(currentUser.getId());
        List<Doctorant> dossiersAValider = doctoratService.getDossiersEnAttenteAvis(currentUser.getId());

        // Map éligibilité soutenance pour chaque doctorant
        Map<Long, Boolean> eligibiliteMap = new HashMap<>();
        for (Doctorant doc : mesDoctorants) {
            eligibiliteMap.put(doc.getId(), doctoratService.verifierEligibiliteSoutenance(doc.getId()));
        }

        // Soutenances en attente de validation admin
        List<Soutenance> soutenancesEnAttente = soutenanceRepository
                .findByStatut(StatutSoutenance.EN_ATTENTE_VALIDATION_ADMIN);

        model.addAttribute("doctorants", mesDoctorants);
        model.addAttribute("dossiersAValider", dossiersAValider);
        model.addAttribute("eligibiliteMap", eligibiliteMap);
        model.addAttribute("soutenancesEnAttente", soutenancesEnAttente);
        model.addAttribute("encadrantNom", currentUser.getNom() + " " + currentUser.getPrenom());

        return "encadrant/dashboard";
    }

    @PostMapping("/valider-inscription")
    public String validerInscription(@RequestParam Long id, @RequestParam boolean favorable) {
        doctoratService.validerDossierParDirecteur(id, favorable);
        return "redirect:/encadrant/dashboard";
    }

    @PostMapping("/proposer-jury")
    public String soumettreJury(@RequestParam Long doctorantId, @RequestParam String proposition) {
        doctoratService.proposerJury(doctorantId, proposition);
        return "redirect:/encadrant/dashboard";
    }
}