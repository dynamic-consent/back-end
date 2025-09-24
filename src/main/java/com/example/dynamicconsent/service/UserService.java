package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.model.UserPreferences;
import com.example.dynamicconsent.domain.repository.UserRepository;
import com.example.dynamicconsent.domain.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    public User getOrCreateUser(String userId) {
        return userRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = new User();
                user.setUserId(userId);
                user.setDisplayName(userId);
                user.setLastLoginAt(Instant.now());
                User savedUser = userRepository.save(user);
                
                // Create default preferences
                UserPreferences preferences = new UserPreferences();
                preferences.setUser(savedUser);
                userPreferencesRepository.save(preferences);
                
                return savedUser;
            });
    }

    public CommonDTOs.UserProfileResponse getUserProfile(String userId) {
        User user = getOrCreateUser(userId);
        UserPreferences preferences = userPreferencesRepository.findByUser(user);
        
        CommonDTOs.UserPreferencesResponse preferencesResponse = preferences != null 
            ? new CommonDTOs.UserPreferencesResponse(
                preferences.getTheme(),
                preferences.getEmailNotifications(),
                preferences.getPushNotifications(),
                preferences.getSmsNotifications(),
                preferences.getRiskThreshold(),
                preferences.getLanguage(),
                preferences.getAutoSync()
            )
            : new CommonDTOs.UserPreferencesResponse(
                "light", true, true, false, 0.7, "ko", true
            );
        
        return new CommonDTOs.UserProfileResponse(
            user.getUserId(),
            user.getDisplayName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getBirthDate(),
            user.getLastConsentChangeAt(),
            user.getLastLoginAt(),
            user.getCreatedAt(),
            preferencesResponse
        );
    }

    @Transactional
    public CommonDTOs.UserProfileResponse updateUserProfile(String userId, CommonDTOs.UserUpdateRequest request) {
        User user = getOrCreateUser(userId);
        
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        
        userRepository.save(user);
        return getUserProfile(userId);
    }

    @Transactional
    public CommonDTOs.UserPreferencesResponse updateUserPreferences(String userId, CommonDTOs.UserPreferencesUpdateRequest request) {
        User user = getOrCreateUser(userId);
        UserPreferences preferences = userPreferencesRepository.findByUser(user);
        
        if (preferences == null) {
            preferences = new UserPreferences();
            preferences.setUser(user);
        }
        
        if (request.theme() != null) {
            preferences.setTheme(request.theme());
        }
        if (request.emailNotifications() != null) {
            preferences.setEmailNotifications(request.emailNotifications());
        }
        if (request.pushNotifications() != null) {
            preferences.setPushNotifications(request.pushNotifications());
        }
        if (request.smsNotifications() != null) {
            preferences.setSmsNotifications(request.smsNotifications());
        }
        if (request.riskThreshold() != null) {
            preferences.setRiskThreshold(request.riskThreshold());
        }
        if (request.language() != null) {
            preferences.setLanguage(request.language());
        }
        if (request.autoSync() != null) {
            preferences.setAutoSync(request.autoSync());
        }
        
        userPreferencesRepository.save(preferences);
        
        return new CommonDTOs.UserPreferencesResponse(
            preferences.getTheme(),
            preferences.getEmailNotifications(),
            preferences.getPushNotifications(),
            preferences.getSmsNotifications(),
            preferences.getRiskThreshold(),
            preferences.getLanguage(),
            preferences.getAutoSync()
        );
    }

    @Transactional
    public void updateLastConsentChange(String userId) {
        User user = getOrCreateUser(userId);
        user.setLastConsentChangeAt(Instant.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String userId) {
        User user = getOrCreateUser(userId);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }
}


