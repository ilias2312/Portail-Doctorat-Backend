package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.Doctorant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorantRepository extends JpaRepository<Doctorant, Long> {
}