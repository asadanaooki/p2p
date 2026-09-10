create table purchase_order_line_allocation (
    pr_detail_id char(36) not null,
    po_line_id char(36) not null,
    allocated_quantity integer not null check(allocated_quantity >= 1),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    
    primary key(pr_detail_id, po_line_id),
    foreign key (pr_detail_id) references purchase_request_detail(pr_detail_id),
    foreign key (po_line_id) references purchase_order_line(po_line_id)
);

create trigger update_purchase_order_line_allocation_modtime
before update on purchase_order_line_allocation
for each row
execute procedure update_timestamp();
