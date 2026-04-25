create table purchase_request_detail (
    pr_detail_id char(36) primary key default gen_random_uuid()::text,
    pr_id char(36) not null,

    item_id char(36),
    snap_item_name varchar(100) not null,
    snap_kind varchar(10) not null,

    unit_id char(36),
    snap_unit_name varchar(50) not null,

    supplier_id char(36),
    snap_supplier_name varchar(100) not null,

    quantity integer not null check(quantity >= 1),
    snap_unit_price integer not null check(snap_unit_price >= 0),
    subtotal integer not null check(subtotal >= 0),

    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,

    foreign key (pr_id) references purchase_request(pr_id),
    foreign key (item_id) references item(item_id),
    foreign key (unit_id) references unit(unit_id),
    foreign key (supplier_id) references supplier(supplier_id)
);

create trigger update_purchase_request_detail_modtime
before update on purchase_request_detail
for each row
execute procedure update_timestamp();

alter table supplier alter column name type varchar(100);

