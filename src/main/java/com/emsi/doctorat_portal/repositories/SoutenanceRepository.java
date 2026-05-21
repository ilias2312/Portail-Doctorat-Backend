package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Soutenance;
import com.emsi.doctorat_portal.entities.Doctorant;
import com.emsi.doctorat_portal.enums.StatutSoutenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {
    Optional<Soutenance> findByDoctorant(Doctorant doctorant);
    List<Soutenance> findByStatut(StatutSoutenance statut);
}