insert into payment_term (
  name,
  days,
  due_date_type,
  is_active
)
  values
  ('30日後払い', 30, 'NET_DAYS', true),
  ('20日後払い', 20, 'NET_DAYS', false),
  ('今月末払い', 31, 'THIS_MONTH_DAY', true),
  ('翌月末払い', 31, 'NEXT_MONTH_DAY', true);
