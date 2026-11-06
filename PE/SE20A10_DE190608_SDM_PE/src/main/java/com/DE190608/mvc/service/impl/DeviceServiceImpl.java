package com.DE190608.mvc.service.impl;

import com.DE190608.mvc.dto.DeviceDTO;
import com.DE190608.mvc.entity.Category;
import com.DE190608.mvc.entity.Device;
import com.DE190608.mvc.repository.CategoryRepository;
import com.DE190608.mvc.repository.DeviceRepository;
import com.DE190608.mvc.service.DeviceService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final DateTimeFormatter DATE_FORMATTER_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository, CategoryRepository categoryRepository) {
        this.deviceRepository = deviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAllByOrderByInstallationDateDesc();
    }

    @Override
    public List<Device> searchByName(String keyword) {
        return deviceRepository.findByDeviceNameContainingIgnoreCaseOrderByInstallationDateDesc(keyword);
    }

    @Override
    public Device getById(Integer id) {
        return deviceRepository.findById(id).orElse(null);
    }

    private LocalDate parseDate(String rawDate) {
        if (rawDate == null) return null;
        String trimmed = rawDate.trim();
        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER_SLASH);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(trimmed, DATE_FORMATTER_DASH);
        }
    }

    @Override
    public void validateBusinessRules(DeviceDTO deviceDTO, BindingResult bindingResult) {
        // Device code duy nhat (unique), check format da valid
        if (deviceDTO.getDeviceCode() != null && !deviceDTO.getDeviceCode().isBlank() && !bindingResult.hasFieldErrors("deviceCode")) {
            Optional<Device> existing = deviceRepository.findByDeviceCode(deviceDTO.getDeviceCode().trim());
            if (existing.isPresent()) {
                bindingResult.addError(new FieldError("deviceDTO", "deviceCode", "Device Code already exists"));
            }
        }

        // Category must exist in the database
        if (deviceDTO.getCategoryId() != null) {
            if (!categoryRepository.existsById(deviceDTO.getCategoryId())) {
                bindingResult.addError(new FieldError("deviceDTO", "categoryId", "Selected category does not exist"));
            }
        }

        // Installation date: format + Ngay cai dat la ngay trong tuong lai
        if (deviceDTO.getInstallationDate() != null && !deviceDTO.getInstallationDate().isBlank()
                && !bindingResult.hasFieldErrors("installationDate")) {
            try {
                LocalDate installationDate = parseDate(deviceDTO.getInstallationDate());
                if (!installationDate.isAfter(LocalDate.now())) {
                    bindingResult.addError(new FieldError("deviceDTO", "installationDate", "Installation date must be a future date"));
                }
            } catch (DateTimeParseException e) {
                bindingResult.addError(new FieldError("deviceDTO", "installationDate", "Invalid date format. Please use dd/MM/yyyy"));
            }
        }
    }

    @Override
    public void createDevice(DeviceDTO deviceDTO) {
        Category category = categoryRepository.findById(deviceDTO.getCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("Category not found with id: " + deviceDTO.getCategoryId())
        );

        Device device = new Device();
        device.setDeviceCode(deviceDTO.getDeviceCode().trim());
        device.setDeviceName(deviceDTO.getDeviceName().trim());
        device.setPrice(deviceDTO.getPrice());
        device.setWarrantyMonths(deviceDTO.getWarrantyMonths());
        device.setInstallationDate(parseDate(deviceDTO.getInstallationDate()));
        device.setCategory(category);
        device.setStatus(deviceDTO.getStatus());

        deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(Integer id) {
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Device not found with id: " + id)
        );

        // Q2.2 - delete a device; throws IllegalStateException when device status is Active (AC)
        if ("AC".equalsIgnoreCase(device.getStatus())) {
            throw new IllegalStateException("Cannot delete an active device");
        }

        deviceRepository.deleteById(id);
    }
}

