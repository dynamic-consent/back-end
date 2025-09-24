package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.Device;
import com.example.dynamicconsent.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUserAndIsActiveTrue(User user);

    Device findByDeviceIdAndIsActiveTrue(String deviceId);

    Device findByUserAndDeviceId(User user, String deviceId);
}
