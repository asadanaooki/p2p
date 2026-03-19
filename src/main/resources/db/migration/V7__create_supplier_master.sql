create table supplier (
    supplier_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(50) not null,
    email varchar(254),
    phone_number varchar(11),
    postal_code char(7),
    prefecture varchar(5),
    city varchar(50),
    street_address varchar(50),
    building_name varchar(50),
    payment_term_id char(36),
    is_active boolean not null default true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    foreign key (payment_term_id) references payment_term(payment_term_id)
);

create trigger update_supplier_modtime
before update on supplier
for each row
execute procedure update_timestamp();