package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.enums.Role;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final DoctorantRepository doctorantRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("erreur", "Les mots de passe ne correspondent pas.");
            return "register";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("erreur", "Cette adresse email est déjà utilisée.");
            return "register";
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.DOCTORANT);

        Doctorant doctorant = new Doctorant();
        doctorant.setNom(nom);
        doctorant.setPrenom(prenom);
        doctorant.setEmail(email);
        doctorant.setDateInscriptionInitiale(LocalDate.now());
        doctorant.setUser(user);
        user.setDoctorant(doctorant);

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}