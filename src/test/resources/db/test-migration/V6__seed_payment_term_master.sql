insert into payment_term (
  payment_term_id,
  name,
  days,
  due_date_type,
  is_active
)
values
  ('33333333-3333-3333-3333-333333333331', '30日後払い', 30, 'NET_DAYS', true),
  ('33333333-3333-3333-3333-333333333332', '20日後払い', 20, 'NET_DAYS', false),
  ('33333333-3333-3333-3333-333333333333', '今月末払い', 31, 'THIS_MONTH_DAY', true),
  ('33333333-3333-3333-3333-333333333334', '翌月末払い', 31, 'NEXT_MONTH_DAY', true);
