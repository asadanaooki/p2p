create table purchase_request_purchase_order (
    pr_id char(36),
    po_id char(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (pr_id, po_id),
    foreign key (pr_id) references purchase_request(pr_id),
    foreign key (po_id) references purchase_order(po_id)
);