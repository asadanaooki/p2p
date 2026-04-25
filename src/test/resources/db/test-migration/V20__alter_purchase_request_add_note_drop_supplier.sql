alter table purchase_request drop column supplier_id;

alter table purchase_request add column note varchar(500);