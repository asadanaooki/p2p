create table approval_workflow (
    approval_workflow_id char(36) primary key,
    document_type varchar(10) not null unique check (document_type in( 'PR', 'PO', 'INVOICE')),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create trigger update_approval_workflow_modtime
before update on approval_workflow
for each row
execute procedure update_timestamp();

