-- Tạo dữ liệu mẫu cho Accounts
-- Tất cả các tài khoản đều có chung password là '12345' (đã được băm bằng BCrypt)
INSERT INTO accounts (id, created_at, updated_at, email, password, account_status, is_email_verified) VALUES
('11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin@stellar.com', '$2a$10$7b.bWJ.wA1I5k5z7U2.Y0ey1U4hCgC5tE3O7M5yY9qg8j/8h5hC8u', 'ACTIVE', true),
('22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manager@stellar.com', '$2a$10$7b.bWJ.wA1I5k5z7U2.Y0ey1U4hCgC5tE3O7M5yY9qg8j/8h5hC8u', 'ACTIVE', true),
('33333333-3333-3333-3333-333333333333', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'customer@stellar.com', '$2a$10$7b.bWJ.wA1I5k5z7U2.Y0ey1U4hCgC5tE3O7M5yY9qg8j/8h5hC8u', 'ACTIVE', true);

-- Tạo Profile tương ứng cho các Account bên trên
INSERT INTO profiles (account_id, id_card_number, full_name, phone_number, gender, birth_date, loyalty_points, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', '079200000001', 'System Admin', '0900000001', 'MALE', '1990-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', '079200000002', 'Stellar Manager', '0900000002', 'FEMALE', '1995-10-15', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', '079200000003', 'Stellar Customer', '0900000003', 'MALE', '1998-05-20', 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Gán role mẫu cho các account này
INSERT INTO account_role (account_id, role_id) VALUES
('11111111-1111-1111-1111-111111111111', (SELECT id FROM roles WHERE code = 'ADMIN')),
('22222222-2222-2222-2222-222222222222', (SELECT id FROM roles WHERE code = 'MANAGER')),
('33333333-3333-3333-3333-333333333333', (SELECT id FROM roles WHERE code = 'CUSTOMER'));
