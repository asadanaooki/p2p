create table purchase_request (
    pr_id char(36) primary key default gen_random_uuid () ::text,
    display_number SERIAL not null unique,
    supplier_id char(36) not null,
    requester_user_id char(36) not null,
    due_date date,
    total_amount integer not null check(total_amount >= 0),
    status varchar(30) not null check(status in ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    foreign key (supplier_id) references supplier(supplier_id),
    foreign key (requester_user_id) references users(user_id)
);

create trigger update_purchase_request_modtime
before update on purchase_request
for each row
execute procedure update_timestamp();