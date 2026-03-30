create table item (
    item_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(100) not null,
    kind varchar(10) check(kind in ('GOODS', 'SERVICE')),
    unit_id char(36),
    price integer check(price >= 0),
    supplier_id char(36),
    description varchar(500),
    is_active boolean not null default true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    foreign key (unit_id) references unit(unit_id),
    foreign key (supplier_id) references supplier(supplier_id)
);

create trigger update_item_modtime
before update on item
for each row
execute procedure update_timestamp();