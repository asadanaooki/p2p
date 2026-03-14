create table users (
    user_id char(36) primary key default gen_random_uuid () ::text,
    email varchar(254) unique not null,
    password_hash varchar(255) not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create or replace function update_timestamp()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language 'plpgsql';

create trigger update_usres_modtime
before update on users
for each row
execute procedure update_timestamp();
