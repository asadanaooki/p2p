drop table if exists unit;

create table unit (
    unit_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(50) unique not null,
    is_active boolean not null default true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create trigger update_unit_modtime
before update on unit
for each row
execute procedure update_timestamp();