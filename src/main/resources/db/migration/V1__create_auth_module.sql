CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_status VARCHAR(50) NOT NULL,
    is_email_verified BOOLEAN NOT NULL
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    name VARCHAR(255) NOT NULL UNIQUE,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE account_role (
    account_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (account_id, role_id)
);

CREATE TABLE role_permission (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_token(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    account_id  UUID NOT NULL,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    device_name  VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL
);

CREATE TABLE otp_code(
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    account_id    UUID,
    email_verified VARCHAR(255) NOT NULL,
    otp_status   VARCHAR(255) NOT NULL,
    otp_hashed    VARCHAR(255) NOT NULL,
    expired_at    TIMESTAMP NOT NULL
);


ALTER TABLE account_role ADD CONSTRAINT fk_account_role_account FOREIGN KEY (account_id) REFERENCES accounts(id);
ALTER TABLE account_role ADD CONSTRAINT fk_account_role_role FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id);

ALTER TABLE refresh_token ADD CONSTRAINT fk_refresh_token_account FOREIGN KEY (account_id) REFERENCES accounts(id);
ALTER TABLE otp_code ADD CONSTRAINT fk_otp_code_account FOREIGN KEY (account_id) REFERENCES accounts(id);
