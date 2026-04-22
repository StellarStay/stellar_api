package system.stellar_stay.modules.properties.enums;

public enum RoomStatus {
    AVAILABLE, // Phòng có sẵn để đặt
    BOOKED, // Phòng đã được đặt bởi khách hàng
    OCCUPIED, // Phòng đang có khách ở
    MAINTENANCE, // Phòng đang được bảo trì hoặc sửa chữa
    OUT_OF_SERVICE // Phòng không hoạt động, có thể do hư hỏng hoặc lý do khác
}
