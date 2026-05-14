package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Soutenance;
import com.emsi.doctorat_portal.entities.Doctorant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {
    Optional<Soutenance> findByDoctorant(Doctorant doctorant);
}