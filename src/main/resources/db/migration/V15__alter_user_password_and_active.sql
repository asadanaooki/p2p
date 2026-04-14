alter table users alter column password_hash drop not null;

alter table users alter column is_active set default false;