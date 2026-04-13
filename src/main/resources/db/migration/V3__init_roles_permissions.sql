-- Insert Roles
INSERT INTO roles (id, created_at, updated_at, code, name, description) VALUES
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'GUEST', 'Guest', 'Người dùng chưa đăng nhập, chỉ có thể xem thông tin phòng và khách sạn.'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CUSTOMER', 'Customer', 'Khách hàng đã đăng ký tài khoản, có thể đặt phòng, đánh giá, quản lý profile.'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'STAFF', 'Staff', 'Nhân viên lễ tân/phục vụ, có thể quản lý booking, check-in, check-out, xem thông tin khách.'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SUB_MANAGER', 'Sub Manager', 'Quản lý cấp dưới, có thể quản lý nhân viên, xem một phần báo cáo doanh thu.'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MANAGER', 'Manager', 'Quản lý chính, có quyền xem toàn bộ báo cáo, quản lý phòng, khách hàng, nhân sự.'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ADMIN', 'Admin', 'Quản trị viên hệ thống, có quyền quản lý toàn bộ hệ thống (gồm thiết lập quyền, thông số hệ thống).');

-- Insert Permissions
-- 1. Booking Permissions
INSERT INTO permissions (id, created_at, updated_at, name, resource, action, description) VALUES
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'BOOKING_CREATE', 'booking', 'create', 'Quyền tạo booking mới'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'BOOKING_READ', 'booking', 'read', 'Quyền xem danh sách hoặc chi tiết booking'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'BOOKING_UPDATE', 'booking', 'update', 'Quyền cập nhật thông tin booking'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'BOOKING_DELETE', 'booking', 'delete', 'Quyền hủy hoặc xóa booking'),

-- 2. Room/Hotel Permissions
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROOM_CREATE', 'room', 'create', 'Quyền thêm phòng mới vào hệ thống'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROOM_READ', 'room', 'read', 'Quyền xem thông tin phòng'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROOM_UPDATE', 'room', 'update', 'Quyền cập nhật trạng thái hoặc thông tin phòng'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROOM_DELETE', 'room', 'delete', 'Quyền xóa phòng khỏi hệ thống'),

-- 3. Account Permissions
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACCOUNT_CREATE', 'account', 'create', 'Quyền tạo tài khoản (thường là cấp quyền cho Admin/Manager)'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACCOUNT_READ', 'account', 'read', 'Quyền xem thông tin tài khoản người dùng'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACCOUNT_UPDATE', 'account', 'update', 'Quyền cập nhật tài khoản (khóa, đổi quyền...)'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACCOUNT_DELETE', 'account', 'delete', 'Quyền xóa tài khoản'),

-- 4. Review Permissions
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'REVIEW_CREATE', 'review', 'create', 'Quyền tạo đánh giá'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'REVIEW_READ', 'review', 'read', 'Quyền xem đánh giá'),
(gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'REVIEW_DELETE', 'review', 'delete', 'Quyền xóa đánh giá (thường dành cho Admin/Manager nếu vi phạm nội quy)');

-- Map Role Permissions
-- GUEST: Chỉ xem phòng, xem booking, xem review
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'GUEST' AND p.name IN ('ROOM_READ', 'BOOKING_READ', 'REVIEW_READ');

-- CUSTOMER: Như GUEST + có thể tạo/sửa booking, tạo review, tự sửa quyền account (Sẽ control logic ở code)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'CUSTOMER' AND p.name IN ('ROOM_READ', 'BOOKING_READ', 'BOOKING_CREATE', 'BOOKING_UPDATE', 'REVIEW_READ', 'REVIEW_CREATE', 'ACCOUNT_READ');

-- STAFF: Như CUSTOMER + quản lý checkin, checkout tức update booking
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'STAFF' AND p.name IN ('ROOM_READ', 'BOOKING_READ', 'BOOKING_UPDATE', 'REVIEW_READ', 'ACCOUNT_READ');

-- SUB_MANAGER: Xem thêm report nâng cao
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'SUB_MANAGER' AND p.name IN ('ROOM_READ', 'ROOM_UPDATE', 'BOOKING_READ', 'BOOKING_UPDATE', 'BOOKING_CREATE', 'REVIEW_READ', 'ACCOUNT_READ');

-- MANAGER: Gần như admin, nhưng không quản lý system config
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'MANAGER' AND p.name != 'ACCOUNT_DELETE';

-- ADMIN: All permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'ADMIN';
