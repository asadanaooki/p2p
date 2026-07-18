-- PRから作成した発注明細に、元となった購買申請明細を関連付ける。
-- 1明細だけを持つ発注と、複数明細をまとめた発注の両方を対象にする。
-- PRを経由せず直接作成した発注明細は関連を持たせない。
insert into purchase_order_line_allocation (
    pr_detail_id,
    po_line_id,
    allocated_quantity
)
select
    prd.pr_detail_id,
    pol.po_line_id,
    prd.quantity
from purchase_request_detail prd
join purchase_request_purchase_order prpo
  on prpo.pr_id = prd.pr_id
join purchase_order po
  on po.po_id = prpo.po_id
 and po.supplier_id = prd.supplier_id
join purchase_order_line pol
  on pol.po_id = po.po_id
 and pol.item_id is not distinct from prd.item_id
 and pol.snap_item_name = prd.snap_item_name
order by po.po_id, pol.line_no, prd.pr_detail_id;

do $$
begin
    if exists (
        select 1
        from purchase_request_detail prd
        join purchase_order_line_allocation allocation
          on allocation.pr_detail_id = prd.pr_detail_id
        group by prd.pr_detail_id, prd.quantity
        having sum(allocation.allocated_quantity) <> prd.quantity
    ) then
        raise exception 'allocated quantity does not match purchase_request_detail quantity';
    end if;

end;
$$;
