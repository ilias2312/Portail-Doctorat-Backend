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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        // On récupère le rôle exact de la base (ex: "ADMINISTRATEUR")
        String roleName = user.getRole().name();

        System.out.println("DEBUG - Tentative de connexion: " + email);
        System.out.println("DEBUG - Rôle trouvé en base: " + roleName);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                // On utilise authorities() pour envoyer la chaîne exacte "ADMINISTRATEUR"
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(roleName)))
                .build();
    }
}