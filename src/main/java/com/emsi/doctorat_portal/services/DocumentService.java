package com.emsi.doctorat_portal.services;

import com.emsi.doctorat_portal.entities.Document;
import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.enums.TypeDocument;
import com.emsi.doctorat_portal.repositories.DoctorantRepository;
import com.emsi.doctorat_portal.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DoctorantRepository doctorantRepository;

    // Dossier de stockage — configurable dans application.yml
    @Value("${upload.dir:uploads}")
    private String uploadDir;

    // ── Uploader un fichier pour un doctorant ──
    public Document sauvegarder(Long doctorantId, MultipartFile fichier, TypeDocument type) throws IOException {

        Doctorant doctorant = doctorantRepository.findById(doctorantId)
                .orElseThrow(() -> new RuntimeException("Doctorant introuvable"));

        // Créer le dossier s'il n'existe pas
        Path dossier = Paths.get(uploadDir, String.valueOf(doctorantId));
        Files.createDirectories(dossier);

        // Générer un nom unique pour éviter les collisions
        String extension = getExtension(fichier.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + extension;
        Path chemin = dossier.resolve(nomFichier);

        Files.copy(fichier.getInputStream(), chemin, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setNom(fichier.getOriginalFilename());
        doc.setCheminFichier(chemin.toString());
        doc.setContentType(fichier.getContentType());
        doc.setType(type);
        doc.setDateDepot(LocalDateTime.now());
        doc.setDoctorant(doctorant);

        return documentRepository.save(doc);
    }

    // ── Récupérer tous les documents d'un doctorant ──
    public List<Document> getDocuments(Long doctorantId) {
        return documentRepository.findByDoctorantId(doctorantId);
    }

    // ── Récupérer les documents par type ──
    public List<Document> getDocumentsByType(Long doctorantId, TypeDocument type) {
        return documentRepository.findByDoctorantIdAndType(doctorantId, type);
    }

    // ── Supprimer un document ──
    public void supprimer(Long documentId) throws IOException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        Path chemin = Paths.get(doc.getCheminFichier());
        Files.deleteIfExists(chemin);
        documentRepository.delete(doc);
    }

    // ── Charger le fichier depuis le disque (pour téléchargement) ──
    public byte[] chargerFichier(Long documentId) throws IOException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        return Files.readAllBytes(Paths.get(doc.getCheminFichier()));
    }

    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}