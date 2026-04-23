package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.services.DoctoratService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctoratService doctoratService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Doctorant> doctorants = doctoratService.getAllDoctorants();
        model.addAttribute("doctorants", doctorants);
        model.addAttribute("total", doctorants.size());
        model.addAttribute("ds", doctoratService); // Important pour les calculs dans le HTML
        return "admin/dashboard";
    }

    @GetMapping("/inscription")
    public String showInscriptionForm(Model model) {
        model.addAttribute("doctorant", new Doctorant());
        return "admin/inscription-doctorant";
    }

    @PostMapping("/inscription/save")
    public String saveDoctorant(@ModelAttribute("doctorant") Doctorant doctorant) {
        doctoratService.saveDoctorant(doctorant);
        return "redirect:/admin/dashboard";
    }
}