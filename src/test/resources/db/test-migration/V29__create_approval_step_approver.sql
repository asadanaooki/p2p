drop table if exists approval_step_approver;

create table approval_step_approver (
    approval_step_approver_id char(36) primary key,
    approval_step_id char(36) not null,
    user_id char(36) not null,
    amount_min integer not null default 0 check (amount_min >= 0),
    amount_max integer check (amount_max >= 0),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    
    unique(approval_step_id, user_id),
    foreign key (approval_step_id) references approval_step(approval_step_id),
    foreign key (user_id) references users(user_id)
);

create trigger update_approval_step_approver_modtime
before update on approval_step_approver
for each row
execute procedure update_timestamp();

