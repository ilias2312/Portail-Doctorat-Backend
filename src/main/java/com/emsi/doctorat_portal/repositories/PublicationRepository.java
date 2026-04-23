package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    // Utile pour récupérer les articles d'un doctorant spécifique
    List<Publication> findByDoctorantId(Long doctorantId);
}