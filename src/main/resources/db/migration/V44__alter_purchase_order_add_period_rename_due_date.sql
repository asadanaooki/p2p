alter table purchase_order add column service_period_from date;
alter table purchase_order add column service_period_to date;

alter table purchase_order rename column due_date to delivery_due_date;

update purchase_order
set delivery_due_date = '2026-06-25'
where po_id = '6f3b9242-2d1e-4e7a-b875-021c3f9a1a01';

update purchase_order
set delivery_due_date = null,
  service_period_from = '2025-03-02',
  service_period_to = '2026-06-22'
where po_id = '98f8c08b-4c55-49fd-b3a1-0a10fdc37a06';

update purchase_order
set delivery_due_date = null,
  service_period_from = '2026-02-01',
  service_period_to = '2026-05-17'
where po_id = 'f7e34df9-f4cb-4c88-82c2-1fdfc7dd8a03';

alter table purchase_order add constraint chk_order_type_columns
check (
    (
    order_type = 'STANDARD'
    and delivery_due_date is not null
    and service_period_from is null
    and service_period_to is null
    )
    or
    (
    order_type = 'SERVICE'
    and delivery_due_date is null
    and service_period_from is not null
    and service_period_to is not null
    )
);