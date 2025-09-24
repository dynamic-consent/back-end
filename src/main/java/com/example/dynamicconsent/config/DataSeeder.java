package com.example.dynamicconsent.config;

import com.example.dynamicconsent.domain.model.*;
import com.example.dynamicconsent.domain.model.enums.*;
import com.example.dynamicconsent.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private ConsentEventRepository eventRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create users
        User user1 = new User();
        user1.setUserId("user1");
        user1.setDisplayName("John Doe");
        user1.setEmail("john@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUserId("user2");
        user2.setDisplayName("Jane Smith");
        user2.setEmail("jane@example.com");
        userRepository.save(user2);

        // Create organizations
        Organization bank1 = new Organization();
        bank1.setOrgId("BANK001");
        bank1.setName("First Bank");
        bank1.setCategory("FINANCE");
        bank1.setDescription("Personal banking services");
        bank1.setIsmsCertified(true);
        organizationRepository.save(bank1);

        Organization bank2 = new Organization();
        bank2.setOrgId("BANK002");
        bank2.setName("Second Bank");
        bank2.setCategory("FINANCE");
        bank2.setDescription("Personal banking services");
        bank2.setIsmsCertified(false);
        organizationRepository.save(bank2);

        Organization hospital = new Organization();
        hospital.setOrgId("HOSPITAL001");
        hospital.setName("City Hospital");
        hospital.setCategory("HEALTHCARE");
        hospital.setDescription("Medical services");
        hospital.setIsmsCertified(true);
        organizationRepository.save(hospital);

        Organization insurance = new Organization();
        insurance.setOrgId("INSURANCE001");
        insurance.setName("Life Insurance");
        insurance.setCategory("INSURANCE");
        insurance.setDescription("Life insurance services");
        insurance.setIsmsCertified(true);
        organizationRepository.save(insurance);

        Organization gov = new Organization();
        gov.setOrgId("GOV001");
        gov.setName("Government Office");
        gov.setCategory("GOVERNMENT");
        gov.setDescription("Government services");
        gov.setIsmsCertified(true);
        organizationRepository.save(gov);

        // Create consents
        Consent consent1 = new Consent();
        consent1.setUser(user1);
        consent1.setOrganization(bank1);
        consent1.setStatus(ConsentStatus.ACTIVE);
        consent1.setSensitivity(DataSensitivity.HIGH);
        consent1.setScopes(Set.of(ConsentScope.PERSONAL_INFO, ConsentScope.FINANCIAL_INFO));
        consent1.setValidFrom(Instant.now().minusSeconds(86400 * 30)); // 30 days ago
        consent1.setValidUntil(Instant.now().plusSeconds(86400 * 365)); // 1 year from now
        consent1.setPurpose("Account opening and financial services");
        consent1.setSharedOrganizationCount(2);
        consentRepository.save(consent1);

        Consent consent2 = new Consent();
        consent2.setUser(user1);
        consent2.setOrganization(hospital);
        consent2.setStatus(ConsentStatus.ACTIVE);
        consent2.setSensitivity(DataSensitivity.HIGH);
        consent2.setScopes(Set.of(ConsentScope.PERSONAL_INFO, ConsentScope.HEALTH_INFO));
        consent2.setValidFrom(Instant.now().minusSeconds(86400 * 15)); // 15 days ago
        consent2.setValidUntil(Instant.now().plusSeconds(86400 * 180)); // 6 months from now
        consent2.setPurpose("Medical treatment and health services");
        consent2.setSharedOrganizationCount(1);
        consentRepository.save(consent2);

        Consent consent3 = new Consent();
        consent3.setUser(user2);
        consent3.setOrganization(insurance);
        consent3.setStatus(ConsentStatus.ACTIVE);
        consent3.setSensitivity(DataSensitivity.MEDIUM);
        consent3.setScopes(Set.of(ConsentScope.PERSONAL_INFO));
        consent3.setValidFrom(Instant.now().minusSeconds(86400 * 7)); // 7 days ago
        consent3.setValidUntil(Instant.now().plusSeconds(86400 * 365)); // 1 year from now
        consent3.setPurpose("Insurance policy management");
        consent3.setSharedOrganizationCount(0);
        consentRepository.save(consent3);

        Consent consent4 = new Consent();
        consent4.setUser(user2);
        consent4.setOrganization(gov);
        consent4.setStatus(ConsentStatus.REVOKED);
        consent4.setSensitivity(DataSensitivity.LOW);
        consent4.setScopes(Set.of(ConsentScope.PERSONAL_INFO));
        consent4.setValidFrom(Instant.now().minusSeconds(86400 * 60)); // 60 days ago
        consent4.setValidUntil(Instant.now().minusSeconds(86400 * 30)); // 30 days ago
        consent4.setPurpose("Basic government services");
        consent4.setRevokeReason("No longer needed");
        consentRepository.save(consent4);

        // Create consent events
        createConsentEvent(consent1, ConsentEventType.CREATED, "Consent created", eventRepository);
        createConsentEvent(consent1, ConsentEventType.SHARED, "Data shared with partner organization", eventRepository);
        createConsentEvent(consent2, ConsentEventType.CREATED, "Consent created", eventRepository);
        createConsentEvent(consent2, ConsentEventType.SHARED, "Data shared with insurance company", eventRepository);
        createConsentEvent(consent3, ConsentEventType.CREATED, "Consent created", eventRepository);
        createConsentEvent(consent4, ConsentEventType.CREATED, "Consent created", eventRepository);
        createConsentEvent(consent4, ConsentEventType.REVOKED, "Consent revoked", eventRepository);

        // Create notices
        Notice notice1 = new Notice();
        notice1.setTitle("System Maintenance Notice");
        notice1.setContent("System will be under maintenance on Sunday from 2AM to 4AM.");
        notice1.setCategory(NoticeCategory.GENERAL);
        notice1.setPriority(1);
        noticeRepository.save(notice1);

        Notice notice2 = new Notice();
        notice2.setTitle("New Feature Release");
        notice2.setContent("We have released new features for better user experience.");
        notice2.setCategory(NoticeCategory.UPDATE);
        notice2.setPriority(2);
        noticeRepository.save(notice2);

        Notice notice3 = new Notice();
        notice3.setTitle("Security Alert");
        notice3.setContent("Please update your password for security reasons.");
        notice3.setCategory(NoticeCategory.URGENT);
        notice3.setPriority(3);
        noticeRepository.save(notice3);
    }

    private void createConsentEvent(Consent consent, ConsentEventType type, String details, ConsentEventRepository eventRepo) {
        ConsentEvent event = new ConsentEvent();
        event.setConsent(consent);
        event.setUser(consent.getUser());
        event.setType(type);
        event.setDetails(details);
        event.setOrganizationName(consent.getOrganization().getName());
        eventRepo.save(event);
    }
}