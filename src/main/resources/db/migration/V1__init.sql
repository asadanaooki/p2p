create table users (
    user_id uuid primary key default gen_random_uuid (),
    email varchar(254) unique not null,
    password_hash varchar(255) not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)