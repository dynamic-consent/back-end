package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Device;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public CommonDTOs.DeviceResponse registerDevice(String userId, CommonDTOs.DeviceRegisterRequest request) {
        User user = new User();
        user.setUserId(userId);
        
        // Check if existing device exists
        Device existingDevice = deviceRepository.findByUserAndDeviceId(user, request.deviceId());
        
        Device device;
        if (existingDevice != null) {
            // Update existing device
            device = existingDevice;
            device.setPushToken(request.pushToken());
            device.setPlatform(request.platform());
            device.setAppVersion(request.appVersion());
            device.setIsActive(true);
        } else {
            // Create new device
            device = new Device();
            device.setUser(user);
            device.setDeviceId(request.deviceId());
            device.setPushToken(request.pushToken());
            device.setPlatform(request.platform());
            device.setAppVersion(request.appVersion());
            device.setIsActive(true);
        }
        
        Device savedDevice = deviceRepository.save(device);
        
        return new CommonDTOs.DeviceResponse(
            savedDevice.getId(),
            savedDevice.getDeviceId(),
            savedDevice.getPlatform(),
            savedDevice.getAppVersion(),
            savedDevice.getIsActive(),
            savedDevice.getCreatedAt()
        );
    }

    public List<CommonDTOs.DeviceResponse> getUserDevices(String userId) {
        User user = new User();
        user.setUserId(userId);
        
        List<Device> devices = deviceRepository.findByUserAndIsActiveTrue(user);
        
        return devices.stream()
                .map(device -> new CommonDTOs.DeviceResponse(
                    device.getId(),
                    device.getDeviceId(),
                    device.getPlatform(),
                    device.getAppVersion(),
                    device.getIsActive(),
                    device.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deactivateDevice(String userId, String deviceId) {
        User user = new User();
        user.setUserId(userId);
        
        Device device = deviceRepository.findByUserAndDeviceId(user, deviceId);
        if (device != null) {
            device.setIsActive(false);
            deviceRepository.save(device);
        }
    }
}