create table payment_term (
    payment_term_id char(36) primary key default gen_random_uuid () ::text,
    name varchar(50) unique not null,
    days integer not null check(days > 0),
    due_date_type varchar(30) not null check(due_date_type in ('NET_DAYS', 'THIS_MONTH_DAY', 'NEXT_MONTH_DAY')),
    is_active boolean not null default true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create trigger update_payment_term_modtime
before update on payment_term
for each row
execute procedure update_timestamp();