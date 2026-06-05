create table approval_task (
    document_id char(36) not null,
    step_order integer not null check(step_order >= 1),
    user_id char(36) not null,
    document_type varchar(10) not null check (document_type in( 'PR', 'PO', 'INVOICE')),
    status varchar(10) not null check (status in( 'PENDING', 'APPROVED', 'REJECTED')),
    comment varchar(500),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    
    primary key (document_id, step_order, user_id),
    foreign key (user_id) references users(user_id)
);

create trigger update_approval_task_modtime
before update on approval_task
for each row
execute procedure update_timestamp();

