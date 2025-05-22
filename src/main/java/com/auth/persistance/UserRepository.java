package com.auth.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthode pour trouver un utilisateur par son coachName
    User findByCoachName(String coachName);
}