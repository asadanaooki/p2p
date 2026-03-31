create table role (
    role_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(50) not null,
    pr_create boolean not null,
    po_create boolean not null,
    receipt_create boolean not null,
    invoice_create boolean not null,
    pr_view_scope varchar(5) not null,
    po_view_scope varchar(5) not null,
    receipt_view_scope varchar(5) not null,
    invoice_view_scope varchar(5) not null,
    pr_approve boolean not null,
    po_approve boolean not null,
    invoice_approve boolean not null,
    setting_manage boolean not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create trigger update_role_modtime
before update on role
for each row
execute procedure update_timestamp();