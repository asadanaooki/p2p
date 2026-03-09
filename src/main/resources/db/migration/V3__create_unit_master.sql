create table unit (
    unit_id char(36) primary key default gen_random_uuid () ::text,
    display_name varchar(50) unique not null,
    official_name varchar(100) unique not null,
    is_active boolean not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)