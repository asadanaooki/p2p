create table purchase_order (
    po_id char(36) primary key,
    display_number SERIAL not null unique,
    purchaser_user_id char(36) not null,
    due_date date,
    supplier_id char(36) not null,
    snap_supplier_name varchar(100) not null,
    total_amount integer not null check(total_amount >= 0),
    status varchar(30) not null check(status in ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    current_step_order integer not null check(current_step_order >= 1),
    note varchar(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    foreign key (supplier_id) references supplier(supplier_id),
    foreign key (purchaser_user_id) references users(user_id)
);

create trigger update_purchase_order_modtime
before update on purchase_order
for each row
execute procedure update_timestamp();