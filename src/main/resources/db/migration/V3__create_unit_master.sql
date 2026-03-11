create table unit (
    unit_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(50) unique not null,
    is_active boolean not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)