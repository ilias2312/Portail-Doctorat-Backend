package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Recherche de l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        // 2. Récupération propre du rôle
        // Si user.getRole() est une Enum, on prend son nom (ex: "ENCADRANT")
        String roleName = user.getRole().name();

        // 3. Normalisation du rôle pour Spring Security
        // Il est crucial que le rôle commence par "ROLE_" pour utiliser .hasRole() dans la config
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        // Debug optionnel pour vérifier en console pendant le développement
        // System.out.println("Utilisateur connecté : " + email + " avec le rôle : " + roleName);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Doit être un hash BCrypt valide en base
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(roleName)))
                .build();
    }
}