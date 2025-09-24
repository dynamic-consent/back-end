package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.UserPreferences;
import com.example.dynamicconsent.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

    UserPreferences findByUser(User user);
}
