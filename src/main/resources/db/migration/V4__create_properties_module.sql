CREATE TABLE properties (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    ward VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL,
    manager_id UUID NOT NULL UNIQUE
);

CREATE TABLE sub_managers (
    sub_manager_id UUID PRIMARY KEY,
    property_id UUID NOT NULL UNIQUE,
    salary_type VARCHAR(50) NOT NULL,
    salary DECIMAL(19, 4) NOT NULL,
    assigned_date DATE NOT NULL,
    is_working BOOLEAN NOT NULL,
    leaved_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE staffs (
    staff_id UUID PRIMARY KEY,
    property_id UUID NOT NULL,
    sub_manager_id UUID NOT NULL,
    position VARCHAR(50) NOT NULL,
    salary_type VARCHAR(50) NOT NULL,
    salary DECIMAL(19, 4) NOT NULL,
    assigned_date DATE NOT NULL,
    is_working BOOLEAN NOT NULL,
    leaved_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    description TEXT,
    max_occupancy INT NOT NULL,
    floor INT NOT NULL,
    area DECIMAL(10, 2),
    base_price DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL,
    property_id UUID NOT NULL
);

CREATE TABLE room_images (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    room_id UUID NOT NULL,
    url VARCHAR(1000) NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    sort_order INT NOT NULL,
    is_primary BOOLEAN NOT NULL
);

CREATE TABLE amenities (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amenities_type VARCHAR(50) NOT NULL
);

CREATE TABLE room_amenities (
    room_id UUID NOT NULL,
    amenities_id UUID NOT NULL,
    PRIMARY KEY (room_id, amenities_id)
);

-- Thêm Foreign Keys
ALTER TABLE properties ADD CONSTRAINT fk_properties_manager FOREIGN KEY (manager_id) REFERENCES accounts(id);

ALTER TABLE sub_managers ADD CONSTRAINT fk_sub_managers_account FOREIGN KEY (sub_manager_id) REFERENCES accounts(id);
ALTER TABLE sub_managers ADD CONSTRAINT fk_sub_managers_property FOREIGN KEY (property_id) REFERENCES properties(id);

ALTER TABLE staffs ADD CONSTRAINT fk_staffs_account FOREIGN KEY (staff_id) REFERENCES accounts(id);
ALTER TABLE staffs ADD CONSTRAINT fk_staffs_property FOREIGN KEY (property_id) REFERENCES properties(id);
ALTER TABLE staffs ADD CONSTRAINT fk_staffs_sub_manager FOREIGN KEY (sub_manager_id) REFERENCES sub_managers(sub_manager_id);

ALTER TABLE rooms ADD CONSTRAINT fk_rooms_property FOREIGN KEY (property_id) REFERENCES properties(id);

ALTER TABLE room_images ADD CONSTRAINT fk_room_images_room FOREIGN KEY (room_id) REFERENCES rooms(id);

ALTER TABLE room_amenities ADD CONSTRAINT fk_room_amenities_room FOREIGN KEY (room_id) REFERENCES rooms(id);
ALTER TABLE room_amenities ADD CONSTRAINT fk_room_amenities_amenities FOREIGN KEY (amenities_id) REFERENCES amenities(id);
