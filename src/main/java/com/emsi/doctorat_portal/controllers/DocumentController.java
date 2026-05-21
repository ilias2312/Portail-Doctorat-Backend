package com.emsi.doctorat_portal.controllers;

import com.emsi.doctorat_portal.entities.Document;
import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.enums.TypeDocument;
import com.emsi.doctorat_portal.repositories.UserRepository;
import com.emsi.doctorat_portal.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    // ── Upload pièce justificative (doctorant connecté) ──
    @PostMapping("/doctorant/document/upload")
    public String uploadPiece(@RequestParam("fichier") MultipartFile fichier,
                              @RequestParam("type") TypeDocument type,
                              Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
            Long doctorantId = user.getDoctorant().getId();
            documentService.sauvegarder(doctorantId, fichier, type);
        } catch (IOException e) {
            return "redirect:/doctorant/dashboard?erreurUpload";
        }
        return "redirect:/doctorant/dashboard?uploadOk";
    }

    // ── Upload manuscrit par le doctorant ──
    @PostMapping("/doctorant/manuscrit/upload")
    public String uploadManuscrit(@RequestParam("fichier") MultipartFile fichier,
                                  Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
            Long doctorantId = user.getDoctorant().getId();
            documentService.sauvegarder(doctorantId, fichier, TypeDocument.MANUSCRIT);
        } catch (IOException e) {
            return "redirect:/doctorant/dashboard?erreurUpload";
        }
        return "redirect:/doctorant/dashboard?uploadOk";
    }

    // ── Télécharger un fichier (admin ou doctorant propriétaire) ──
    @GetMapping("/document/telecharger/{id}")
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        try {
            Document doc = documentService.findById(id);
            byte[] data = documentService.chargerFichier(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getNom() + "\"")
                    .contentType(MediaType.parseMediaType(
                            doc.getContentType() != null ? doc.getContentType() : "application/octet-stream"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Supprimer un document (doctorant propriétaire) ──
    @GetMapping("/doctorant/document/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        try {
            documentService.supprimer(id);
        } catch (IOException e) {
            return "redirect:/doctorant/dashboard?erreurSuppression";
        }
        return "redirect:/doctorant/dashboard";
    }
}