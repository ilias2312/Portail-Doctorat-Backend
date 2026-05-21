package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Document;
import com.emsi.doctorat_portal.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByDoctorantId(Long doctorantId);
    List<Document> findByDoctorantIdAndType(Long doctorantId, TypeDocument type);
}