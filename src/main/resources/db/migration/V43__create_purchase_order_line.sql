drop table if exists purchase_order_line;

create table purchase_order_line (
    po_line_id char(36) primary key,
    po_id char(36) not null,
    line_no integer not null check(line_no >= 1),
    item_id varchar(36),
    unit_id char(36),
    snap_item_name varchar(100) not null,
    line_amount_excluding_tax integer not null check(line_amount_excluding_tax>=0),
    
    -- POのみ
    snap_unit_name varchar(50),
    snap_unit_price integer check(snap_unit_price>=0),
    quantity integer check(quantity>=1),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    foreign key (po_id) references purchase_order(po_id),
    foreign key (item_id) references item(item_id),
    foreign key (unit_id) references unit(unit_id)
);

drop trigger if exists trg_po_line_validate_standard_po_columns on purchase_order_line;

drop function if exists validate_standard_po_line_columns();

create or replace function validate_standard_po_line_columns()
returns trigger as $$
declare
  v_order_type varchar;
begin
  select order_type
    into v_order_type
    from purchase_order
   where po_id = new.po_id;

  if v_order_type = 'STANDARD' then

    if new.snap_unit_name is null
       or new.snap_unit_price is null
       or new.quantity is null then

      raise exception 'standard_columns_error. po_id=%, line_no=%',
        new.po_id, new.line_no;

    end if;

  end if;

  return new;
end;
$$ language plpgsql;

create trigger trg_po_line_validate_standard_po_columns
before insert or update on purchase_order_line
for each row
execute function validate_standard_po_line_columns();