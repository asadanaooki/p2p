insert into approval_step (
  approval_step_id,
  approval_workflow_id,
  name,
  step_order
) values
-- PR：購買申請 1ステップ
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', '申請内容確認', 1),

-- INVOICE：請求書 2ステップ
('cccccccc-cccc-cccc-cccc-ccccccccccc1', '33333333-3333-3333-3333-333333333333', '請求内容確認', 1),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', '33333333-3333-3333-3333-333333333333', '支払承認', 2),

-- PO：発注 3ステップ
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '22222222-2222-2222-2222-222222222222', '発注内容確認', 1),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', '22222222-2222-2222-2222-222222222222', '購買責任者承認', 2),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', '22222222-2222-2222-2222-222222222222', '最終発注承認', 3);