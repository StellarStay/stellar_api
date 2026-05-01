-- 1. Insert Amenities (Các tiện ích chung và riêng)
INSERT INTO amenities (id, created_at, updated_at, name, description, amenities_type) VALUES
('44444444-1111-1111-1111-111111111111', NOW(), NOW(), 'Free WiFi', 'High-speed internet connection', 'ROOM'),
('44444444-2222-2222-2222-222222222222', NOW(), NOW(), 'Air Conditioning', 'Controls room temperature', 'ROOM'),
('44444444-3333-3333-3333-333333333333', NOW(), NOW(), 'Flat Screen TV', 'TV with cable channels', 'ROOM'),
('44444444-4444-4444-4444-444444444444', NOW(), NOW(), 'Mini Bar', 'Stocked with drinks and snacks', 'ROOM'),
('44444444-5555-5555-5555-555555555555', NOW(), NOW(), 'Balcony', 'Private balcony with nice view', 'ROOM'),
('44444444-6666-6666-6666-666666666666', NOW(), NOW(), 'Swimming Pool', 'Outdoor private pool', 'PROPERTY');

-- 2. Insert Properties (3 Properties: 1 Hotel, 1 Homestay, 1 Resort)
INSERT INTO properties (id, created_at, updated_at, name, slug, type, description, address, city, district, ward, latitude, longitude, phone, email, status, is_available, manager_id)
VALUES
('11111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Stellar Luxury Hotel', 'stellar-luxury-hotel', 'HOTEL', 'A luxury 5-star hotel in the heart of the city.', '123 Nguyen Hue', 'Ho Chi Minh City', 'District 1', 'Ben Nghe', 10.7769000, 106.7009000, '0123456789', 'contact@stellarhotel.com', 'ACTIVE', true, (SELECT id FROM accounts WHERE email = 'admin@stellar.com' LIMIT 1)),
('22222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Cozy Homestay Da Lat', 'cozy-homestay-da-lat', 'HOMESTAY', 'A beautiful homestay with pine forest view.', '45 Tran Hung Dao', 'Da Lat', 'Phuong 10', 'Ward 10', 11.9404000, 108.4583000, '0987654321', 'booking@cozydalat.com', 'ACTIVE', true, (SELECT id FROM accounts WHERE email = 'manager@stellar.com' LIMIT 1)),
('33333333-3333-3333-3333-333333333333', NOW(), NOW(), 'Sunrise Resort Da Nang', 'sunrise-resort-da-nang', 'RESORT', 'Beachfront resort with premium services.', '100 Vo Nguyen Giap', 'Da Nang', 'Son Tra', 'My An', 16.0544000, 108.2022000, '0236123456', 'hello@sunriseresort.com', 'ACTIVE', true, (SELECT id FROM accounts WHERE email = 'customer@stellar.com' LIMIT 1));

-- 3. Insert Rooms (Mỗi property 4 rooms)
INSERT INTO rooms (id, created_at, updated_at, name, room_number, room_type, description, max_occupancy, floor, area, base_price, currency, is_available, property_id)
VALUES
-- P1: Stellar Luxury Hotel
('c1110000-0000-0000-0000-000000000000', NOW(), NOW(), 'Presidential Suite', 'P-999', 'LUXURY', 'Top floor suite with city view.', 4, 9, 120.50, 5000000.00, 'VND',  true, '11111111-1111-1111-1111-111111111111'),
('c1120000-0000-0000-0000-000000000000', NOW(), NOW(), 'Deluxe Double', 'D-201', 'DOUBLE', 'Comfortable double room for couples.', 2, 2, 45.00, 1500000.00, 'VND', false, '11111111-1111-1111-1111-111111111111'),
('c1130000-0000-0000-0000-000000000000', NOW(), NOW(), 'Executive Twin', 'E-301', 'TWIN', 'Twin beds with modern setup.', 2, 3, 40.00, 1300000.00, 'VND', true, '11111111-1111-1111-1111-111111111111'),
('c1140000-0000-0000-0000-000000000000', NOW(), NOW(), 'Standard Single', 'S-101', 'SINGLE', 'Basic room for solo traveler.', 1, 1, 25.00, 800000.00, 'VND', true, '11111111-1111-1111-1111-111111111111'),

-- P2: Cozy Homestay Da Lat
('c2210000-0000-0000-0000-000000000000', NOW(), NOW(), 'Pine View Single', '101', 'SINGLE', 'Cozy single room viewing pine trees.', 1, 1, 20.00, 500000.00, 'VND', false, '22222222-2222-2222-2222-222222222222'),
('c2220000-0000-0000-0000-000000000000', NOW(), NOW(), 'Valley Double', '201', 'DOUBLE', 'Double room looking to the valley.', 2, 2, 35.00, 800000.00, 'VND', true, '22222222-2222-2222-2222-222222222222'),
('c2230000-0000-0000-0000-000000000000', NOW(), NOW(), 'Attic Twin', '301', 'TWIN', 'Twin beds located in the attic.', 2, 3, 30.00, 750000.00, 'VND',  true, '22222222-2222-2222-2222-222222222222'),
('c2240000-0000-0000-0000-000000000000', NOW(), NOW(), 'Family Room', '102', 'FAMILY', 'Large room suitable for family of 4.', 4, 1, 55.00, 1200000.00, 'VND',  false, '22222222-2222-2222-2222-222222222222'),

-- P3: Sunrise Resort Da Nang
('c3310000-0000-0000-0000-000000000000', NOW(), NOW(), 'Ocean View Villa', 'V-01', 'TWIN', 'Private villa with direct beach access.', 6, 1, 200.00, 8000000.00, 'VND', true, '33333333-3333-3333-3333-333333333333'),
('c3320000-0000-0000-0000-000000000000', NOW(), NOW(), 'Garden Suite', 'G-10', 'DOUBLE', 'Suite surrounded by tropical garden.', 4, 1, 80.00, 3500000.00, 'VND', false, '33333333-3333-3333-3333-333333333333'),
('c3330000-0000-0000-0000-000000000000', NOW(), NOW(), 'Poolside Double', 'P-05', 'FAMILY', 'Steps away from the main resort pool.', 2, 1, 45.00, 2000000.00, 'VND',  true, '33333333-3333-3333-3333-333333333333'),
('c3340000-0000-0000-0000-000000000000', NOW(), NOW(), 'Standard Resort Twin', 'R-11', 'TWIN', 'Standard room with twin beds.', 2, 2, 35.00, 1500000.00, 'VND', true, '33333333-3333-3333-3333-333333333333');

-- 4. Insert Room Images (Mỗi phòng 3 ảnh: 1 primary, 2 normal)
INSERT INTO room_images (id, created_at, updated_at, room_id, url, media_type, sort_order, is_primary) VALUES
-- R11
(gen_random_uuid(), NOW(), NOW(), 'c1110000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1590490360182-c33d57733427', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c1110000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c1110000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304', 'IMAGE', 3, false),
-- R12
(gen_random_uuid(), NOW(), NOW(), 'c1120000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1611892440504-42a792e24d32', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c1120000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1595576508898-0ad5c879a061', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c1120000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1566665797739-1674de7a421a', 'IMAGE', 3, false),
-- R13
(gen_random_uuid(), NOW(), NOW(), 'c1130000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1549294413-26f195200c16', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c1130000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1608198399988-341cbfa0ebbc', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c1130000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1522771731478-4ea767a14a2b', 'IMAGE', 3, false),
-- R14
(gen_random_uuid(), NOW(), NOW(), 'c1140000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1505691938895-1758d7feb511', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c1140000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1540518614846-7eded433c457', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c1140000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1554995207-c18c203602cb', 'IMAGE', 3, false),

-- R21
(gen_random_uuid(), NOW(), NOW(), 'c2210000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c2210000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c2210000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1497366216548-37526070297c', 'IMAGE', 3, false),
-- R22
(gen_random_uuid(), NOW(), NOW(), 'c2220000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c2220000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1502005229762-cf1b2e1c6b49', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c2220000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1484154218962-a197022b5858', 'IMAGE', 3, false),
-- R23
(gen_random_uuid(), NOW(), NOW(), 'c2230000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1502672260266-1c1f52d3aa05', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c2230000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c2230000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1510798831971-661eb04b3739', 'IMAGE', 3, false),
-- R24
(gen_random_uuid(), NOW(), NOW(), 'c2240000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1531834685032-c34bf0d84c77', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c2240000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c2240000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1507089947368-19c1da9775ae', 'IMAGE', 3, false),

-- R31
(gen_random_uuid(), NOW(), NOW(), 'c3310000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1574643034960-449e7a4cd559', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c3310000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1601628828688-632f38a5a7d0', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c3310000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1594569584347-197e937d1a1b', 'IMAGE', 3, false),
-- R32
(gen_random_uuid(), NOW(), NOW(), 'c3320000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c3320000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1564078516393-cf04bd966897', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c3320000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1616046229478-9901c5536a45', 'IMAGE', 3, false),
-- R33
(gen_random_uuid(), NOW(), NOW(), 'c3330000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c3330000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1533759413974-9e15f3b745ac', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c3330000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1600334089648-b0a3d4d420f1', 'IMAGE', 3, false),
-- R34
(gen_random_uuid(), NOW(), NOW(), 'c3340000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1592233152615-5c1cfacb15cc', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), 'c3340000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1612152062624-9b2184e1b7b5', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), 'c3340000-0000-0000-0000-000000000000', 'https://images.unsplash.com/photo-1551298370-9d3d53740c72', 'IMAGE', 3, false);

-- 5. Insert Room Amenities (Gắn tiện ích cho 12 phòng)
INSERT INTO room_amenities (room_id, amenities_id) VALUES
-- R11: Cấu hình Max đồ chơi
('c1110000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c1110000-0000-0000-0000-000000000000', '44444444-2222-2222-2222-222222222222'),
('c1110000-0000-0000-0000-000000000000', '44444444-3333-3333-3333-333333333333'),
('c1110000-0000-0000-0000-000000000000', '44444444-4444-4444-4444-444444444444'),
('c1110000-0000-0000-0000-000000000000', '44444444-5555-5555-5555-555555555555'),
-- R12, R13, R14
('c1120000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c1120000-0000-0000-0000-000000000000', '44444444-2222-2222-2222-222222222222'),
('c1130000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c1130000-0000-0000-0000-000000000000', '44444444-2222-2222-2222-222222222222'),
('c1140000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
-- P2 Rooms (R21-R24)
('c2210000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c2220000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c2230000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c2240000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c2240000-0000-0000-0000-000000000000', '44444444-3333-3333-3333-333333333333'),
-- P3 Rooms (R31-R34)
('c3310000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c3310000-0000-0000-0000-000000000000', '44444444-2222-2222-2222-222222222222'),
('c3310000-0000-0000-0000-000000000000', '44444444-5555-5555-5555-555555555555'),
('c3320000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c3330000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111'),
('c3340000-0000-0000-0000-000000000000', '44444444-1111-1111-1111-111111111111');

-- 6. Insert Property Images (Mỗi property 3 ảnh: 1 primary, 2 normal)
INSERT INTO property_images (id, created_at, updated_at, property_id, url, media_type, sort_order, is_primary) VALUES
-- P1: Stellar Luxury Hotel
(gen_random_uuid(), NOW(), NOW(), '11111111-1111-1111-1111-111111111111', 'https://images.unsplash.com/photo-1566073771259-6a8506099945', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), '11111111-1111-1111-1111-111111111111', 'https://images.unsplash.com/photo-1542314831-c6a4d271970b', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), '11111111-1111-1111-1111-111111111111', 'https://images.unsplash.com/photo-1496417263034-38ec4f0b665a', 'IMAGE', 3, false),

-- P2: Cozy Homestay Da Lat
(gen_random_uuid(), NOW(), NOW(), '22222222-2222-2222-2222-222222222222', 'https://images.unsplash.com/photo-1501183638710-841dd1904471', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), '22222222-2222-2222-2222-222222222222', 'https://images.unsplash.com/photo-1449844908441-8829872d2607', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), '22222222-2222-2222-2222-222222222222', 'https://images.unsplash.com/photo-1510798831971-661eb04b3739', 'IMAGE', 3, false),

-- P3: Sunrise Resort Da Nang
(gen_random_uuid(), NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b', 'IMAGE', 1, true),
(gen_random_uuid(), NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'https://images.unsplash.com/photo-1563911302283-d2bc129e7570', 'IMAGE', 2, false),
(gen_random_uuid(), NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9', 'IMAGE', 3, false);
