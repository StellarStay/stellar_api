CREATE TABLE profiles (
    account_id UUID PRIMARY KEY,
    id_card_number VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    avatar_url  VARCHAR(255),
    gender VARCHAR(50),
    birth_date DATE,
    loyalty_points INT NOT NULL DEFAULT 0
);

ALTER TABLE profiles ADD CONSTRAINT fk_profiles_account FOREIGN KEY (account_id) REFERENCES accounts(id);