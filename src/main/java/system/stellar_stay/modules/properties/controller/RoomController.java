package system.stellar_stay.modules.properties.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseForManagerDTO;
import system.stellar_stay.modules.properties.service.RoomService;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    // For Manager

    // 1. Create Room
    @Operation(summary = "Create a new room for a property", description = """
            API này dùng để tạo mới một phòng cho một property cụ thể. 
            Manager sẽ cung cấp thông tin chi tiết về phòng như tên, số phòng, loại phòng, mô tả, sức chứa tối đa, diện tích, giá cơ bản và đơn vị tiền tệ. 
            API sẽ kiểm tra tính hợp lệ của propertyId và sau đó tạo mới phòng dựa trên thông tin đã cung cấp. 
            Nếu thành công, API sẽ trả về chi tiết của phòng vừa được tạo.
            """)
    @PostMapping("/manager/{id}/property")
    public ResponseEntity<ApiResponse<RoomResponseForManagerDTO>> createRoomForManager(HttpServletRequest request,
                                                                                       @PathVariable("id") UUID propertyId,
                                                                                       @RequestBody RoomCreateRequestForManagerDTO roomCreateRequestForManagerDTO) {

        return ResponseEntity.ok(
                ApiResponse.<RoomResponseForManagerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Create room successfully")
                        .result(roomService.createRoomForManager(propertyId, roomCreateRequestForManagerDTO))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 2. Update Room
    @Operation(summary = "Update room for a property", description = """
            API này dùng để cập nhật thông tin của một phòng đã tồn tại. 
            Manager sẽ cung cấp thông tin chi tiết về phòng như tên, số phòng, loại phòng, mô tả, sức chứa tối đa, diện tích, giá cơ bản và đơn vị tiền tệ. 
            API sẽ kiểm tra tính hợp lệ của roomId và sau đó cập nhật thông tin phòng dựa trên thông tin đã cung cấp. 
            Nếu thành công, API sẽ trả về chi tiết của phòng vừa được cập nhật.
            """)
    @PutMapping("/manager/{id}")
    public ResponseEntity<ApiResponse<RoomResponseForManagerDTO>> updateRoomForManager(HttpServletRequest request,
                                                                                             @PathVariable("id") UUID roomId,
                                                                                             @RequestBody RoomUpdateRequestForManagerDTO roomUpdateRequestForManagerDTO) {

        return ResponseEntity.ok(
                ApiResponse.<RoomResponseForManagerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update room successfully")
                        .result(roomService.updateRoomForManager(roomId, roomUpdateRequestForManagerDTO))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 3. Get room in properties
    @Operation(summary = "Get all room in property for a property", description = """
            API này dùng để lấy tất cả các phòng trong một property cụ thể.         @
            Manager sẽ cung cấp propertyId để xác định property mà họ muốn lấy thông tin phòng. 
            API sẽ kiểm tra tính hợp lệ của propertyId và sau đó truy vấn cơ sở dữ liệu để lấy tất cả các phòng thuộc property đó. 
            API sẽ trả về một danh sách các phòng cùng với thông tin chi tiết của từng phòng, bao gồm tên, số phòng, loại phòng, mô tả, sức chứa tối đa, diện tích, giá cơ bản và đơn vị tiền tệ.
            """)
    @GetMapping("/manager/property/{propertyId}")
    public ResponseEntity<ApiResponse<Page<ListRoomResponseDTO>>> getAllRoomInPropertyForManager(HttpServletRequest request,
                                                                                                 @PathVariable("propertyId") UUID propertyId,
                                                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                 @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                                 @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                                 @RequestParam(value = "keyword", required = false) String keyword) {

        return ResponseEntity.ok(
                ApiResponse.<Page<ListRoomResponseDTO>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update room successfully")
                        .result(roomService.getAllRoomsForManager(page, size, sort, sortDir, keyword, propertyId))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }




    // For public or customer

    // 1. Get room in properties
    @Operation(summary = "Get all room in property for a property", description = """
            API này dùng để lấy tất cả các phòng trong một property cụ thể.         @
            Ng dùng sẽ cung cấp propertyId để xác định property mà họ muốn lấy thông tin phòng.
            API sẽ kiểm tra tính hợp lệ của propertyId và sau đó truy vấn cơ sở dữ liệu để lấy tất cả các phòng thuộc property đó. 
            API sẽ trả về một danh sách các phòng cùng với thông tin chi tiết của từng phòng, bao gồm tên, số phòng, loại phòng, mô tả, sức chứa tối đa, diện tích, giá cơ bản và đơn vị tiền tệ.
            """)
    @GetMapping("customer/property/{propertyId}")
    public ResponseEntity<ApiResponse<Page<ListRoomResponseDTO>>> getAllRoomInPropertyForPublic(HttpServletRequest request,
                                                                                                 @PathVariable("propertyId") UUID propertyId,
                                                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                 @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                                 @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                                 @RequestParam(value = "keyword", required = false) String keyword) {

        return ResponseEntity.ok(
                ApiResponse.<Page<ListRoomResponseDTO>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update room successfully")
                        .result(roomService.getAllRoomsForPublic(page, size, sort, sortDir, keyword, propertyId))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 2. Get detail room for customer
    @Operation(summary = "Get detail room for customer or public", description = """
            API này dùng để lấy chi tiết thông tin của một phòng cụ thể.
            Ng dùng sẽ cung cấp roomId để xác định phòng mà họ muốn lấy thông tin chi tiết. 
            API sẽ kiểm tra tính hợp lệ của roomId và sau đó truy vấn cơ sở dữ liệu để lấy thông tin chi tiết của phòng đó. 
            API sẽ trả về thông tin chi tiết của phòng, bao gồm tên, số phòng, loại phòng, mô tả, sức chứa tối đa, diện tích, giá cơ bản và đơn vị tiền tệ, cùng với danh sách hình ảnh của phòng (nếu có) và các tiện nghi đi kèm (nếu có).
            """)
    @GetMapping("customer/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponseDetailForCustomerDTO>> getDetailRoomForPublic(HttpServletRequest request,
                                                                                                @PathVariable("roomId") UUID roomId) {

        return ResponseEntity.ok(
                ApiResponse.<RoomResponseDetailForCustomerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update room successfully")
                        .result(roomService.getDetailRoomForCustomer(roomId))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

}
