package com.emsi.doctorat_portal.repositories;

import com.emsi.doctorat_portal.entities.User;
import com.emsi.doctorat_portal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}