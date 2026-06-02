create table approval_step (
    approval_step_id char(36) primary key,
    approval_workflow_id char(36) not null,
    name varchar(100) not null,
    step_order integer not null check (step_order >= 1),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    
    unique(approval_workflow_id, step_order),
    foreign key (approval_workflow_id) references approval_workflow(approval_workflow_id)
);

create trigger update_approval_step_modtime
before update on approval_step
for each row
execute procedure update_timestamp();

