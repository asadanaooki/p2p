alter table purchase_request_detail
 add column if not exists line_no integer;
 
update purchase_request_detail prd
set line_no = numbered.line_no
from (
  select
    pr_detail_id,
    row_number() over (partition by pr_id order by created_at asc, pr_detail_id asc) as line_no
  from purchase_request_detail
  ) numbered
where prd.pr_detail_id = numbered.pr_detail_id;

alter table purchase_request_detail alter column line_no set not null;

alter table purchase_request_detail add constraint purchase_request_detail_line_no_check
check (line_no >= 1);