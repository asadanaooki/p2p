drop table if exists users;

create table users (
    user_id char(36) primary key default gen_random_uuid () ::text,
    last_name varchar(50) not null,
    first_name varchar(50) not null,
    last_name_kana varchar(50) not null,
    first_name_kana varchar(50) not null,
    email varchar(254) not null unique,
    password_hash varchar(255) not null,
    role_id char(36) not null,
    is_active boolean not null default true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    foreign key (role_id) references role(role_id)
);

create trigger update_users_modtime
before update on users
for each row
execute procedure update_timestamp();