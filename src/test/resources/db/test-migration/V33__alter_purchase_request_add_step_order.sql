alter table purchase_request add column current_step_order
  integer check(current_step_order >= 1);
  
UPDATE purchase_request SET current_step_order = 1;

ALTER TABLE purchase_request ALTER COLUMN current_step_order SET NOT NULL;