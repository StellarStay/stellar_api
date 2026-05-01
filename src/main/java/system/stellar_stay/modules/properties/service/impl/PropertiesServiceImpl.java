package system.stellar_stay.modules.properties.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.service.impl.AuthServiceImpl;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.*;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
import system.stellar_stay.modules.properties.entity.PropertyImageEntity;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;
import system.stellar_stay.modules.properties.mapper.PropertyMapper;
import system.stellar_stay.modules.properties.repository.PropertyRepository;
import system.stellar_stay.modules.properties.service.PropertiesService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.service.EmailService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertiesServiceImpl implements PropertiesService {

    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;
    private final AuthServiceImpl authService;
    private final AccountRepository accountRepository;
    private final EmailService emailService;




// FOR MANAGER

    @Override
    public PropertiesResponseForManagerDTO requestCreatePropertiesForManager(PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO) {
        if(propertyCreateRequestForManagerDTO == null) {
            log.error("PropertyRequestForManagerDTO is null");
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Request is not null");
        }
        PropertiesEntity properties = propertyMapper.toPropertyEntity(propertyCreateRequestForManagerDTO);
        // Lúc này properties sẽ chưa có managerId, status và isAvailable, cần set thêm các trường này trước khi lưu vào database
        // Lúc này ta cần lấy ra accountId từ token của người dùng đang đăng nhập
        UUID managerId = authService.extractAccountIdFromContextHolder();
        if(managerId == null){
            throw new ApiException(ErrorCode.UNAUTHENTICATED, "User is not authenticated. Please login to continue.");
        }
        Account account = accountRepository.findById(managerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found for the authenticated user."));

        properties.setAccount(account);
        properties.setStatus(PropertiesStatus.PENDING);
        properties.setAvailable(false);
        propertyRepository.save(properties);

        // Gửi email thông báo cho Admin
        emailService.sendRequestCreatePropertyEmail(properties);

        return propertyMapper.toPropertyResponseForManagerDTO(properties);
    }

    @Override
    public PropertiesResponseForManagerDTO updatePropertiesForManager(UUID propertyId, PropertiesUpdateRequestForManagerDTO propertyUpdateRequestForManagerDTO) {
        if(propertyId == null || propertyUpdateRequestForManagerDTO == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyId or Request is not null");
        }
        PropertiesEntity properties = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        // Check xem properties này có thuộc về manager đang đăng nhập không ?
        // Lấy ra accountId
        UUID managerId = authService.extractAccountIdFromContextHolder();
        if (!managerId.equals(properties.getAccount().getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "You do not have permission to update this property");
        }
        // Chỗ này thay vì manager truyền status thì để hoàn toàn status cho admin quản lý, và manager quản lý cái is_available thôi
        propertyMapper.updatePropertyByManager(propertyUpdateRequestForManagerDTO, properties);
        propertyRepository.save(properties);
        return propertyMapper.toPropertyResponseForManagerDTO(properties);
    }

    @Override
    public Page<ListPropertiesResponseForManager> getPropertiesByManagerId(int page, int size, String sortBy, String sortDir, String keyword) {
        UUID managerId = authService.extractAccountIdFromContextHolder();
        if(managerId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "ManagerId is null");
        }
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PropertiesEntity> listProperty = propertyRepository.findPropertyByManagerId(managerId, keyword, pageable);
        return listProperty.map(
                property ->{
                    ListPropertiesResponseForManager propertyResponse = propertyMapper.toPropertyListResponseForManager(property);
                    property.getImages().stream()
                            .filter(PropertyImageEntity::isPrimary)
                            .findFirst()
                            .ifPresent(thumbnailImage -> propertyResponse.setUrlThumbnailImage(thumbnailImage.getUrl()));
                    return propertyResponse;
                }
        );
    }

    @Override
    public PropertyDetailResponseForManagerDTO getDetailPropertiesByIdForManager(UUID propertyId) {
        if(propertyId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyId or Request is not null");
        }
        PropertiesEntity properties = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        // Check xem properties này có thuộc về manager đang đăng nhập không ?
        // Lấy ra accountId
        UUID managerId = authService.extractAccountIdFromContextHolder();
        if (!managerId.equals(properties.getAccount().getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "This property does not belong to the manager");
        }

        PropertyDetailResponseForManagerDTO propertyDetail = propertyMapper.toPropertyResponseDetailForManagerDTO(properties);
        List<PropertyImageEntity> images = properties.getImages();
        propertyDetail.setListPropertyImage(propertyMapper.toPropertyImageResponseDTO(images));

        return propertyDetail;
    }








// FOR ADMIN
    @Override
    public void updatedStatusPropertiesForRequest(UUID propertyId, PropertiesStatus status, String reason) {

        if(propertyId == null || status == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyId or status");
        }

        PropertiesEntity properties = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));


        if (status == PropertiesStatus.REJECTED) {
            properties.setStatus(PropertiesStatus.REJECTED);
            properties.setAvailable(false);
            propertyRepository.save(properties);

            emailService.sendResultCreatePropertyEmail(properties, reason);
        }
        else if (status == PropertiesStatus.ACTIVE) {
            properties.setStatus(PropertiesStatus.ACTIVE);
            properties.setAvailable(true);
            propertyRepository.save(properties);

            emailService.sendResultCreatePropertyEmail(properties, null);
        }
    }

    @Override
    public void updatePropertiesStatusForAdmin(UUID propertyId, PropertiesStatus status) {
        if(propertyId == null || status == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyId or status is not null");
        }

        PropertiesEntity properties = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        properties.setStatus(status);
        propertyRepository.save(properties);
    }

    @Override
    public Page<ListPropertiesResponseForAdmin> getAllPropertiesForAdmin(int page, int size, String sortBy, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PropertiesEntity> propertiesEntityPage = propertyRepository.getAllProperties(keyword, pageable);

        // Chỗ này phải từ properties entity, lấy ra account rồi map vô manager nữa nha cu -> map thủ công vào


        return propertiesEntityPage.map(property -> {
            ListPropertiesResponseForAdmin dto = propertyMapper.toListResponseForAdmin(property);
            property.getImages().stream()
                    .filter(PropertyImageEntity::isPrimary)
                    .findFirst()
                    .ifPresent(thumbnailImage -> dto.setUrlThumbnailImage(thumbnailImage.getUrl()));
            // Lấy thông tin manager từ properties.getAccount()
            dto.setManager(propertyMapper.managerResponseFromAccount(property.getAccount()));
            return dto;
        });
    }

    @Override
    public PropertyDetailResponseForAdminDTO getDetailPropertiesByIdForAdmin(UUID propertyId) {
        if(propertyId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyId is not null");
        }
        PropertiesEntity properties = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        PropertyDetailResponseForAdminDTO propertyDetail = propertyMapper.toPropertyResponseDetailForAdminDTO(properties);
        List<PropertyImageEntity> images = properties.getImages();
        propertyDetail.setListPropertyImage(propertyMapper.toPropertyImageResponseDTO(images));
        // Lấy thông tin manager từ properties.getAccount()
        propertyDetail.setManager(propertyMapper.managerResponseFromAccount(properties.getAccount()));
        return propertyDetail;
    }
}
