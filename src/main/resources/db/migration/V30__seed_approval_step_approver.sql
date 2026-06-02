insert into approval_step_approver (
    approval_step_approver_id,
    approval_step_id,
    user_id,
    amount_min,
    amount_max
) values
-- PR：購買申請 / 申請内容確認
('dddddddd-dddd-dddd-dddd-dddddddddd01',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
 '2ede13b1-b676-4333-9e8f-d9b361c884d4',
 0,
 null),

-- INVOICE：請求書 / 請求内容確認
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee01',
 'cccccccc-cccc-cccc-cccc-ccccccccccc1',
 'e1ed196d-4f90-45ca-8f07-7fb4ba55ea62',
 0,
 null),

-- INVOICE：請求書 / 支払承認
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee02',
 'cccccccc-cccc-cccc-cccc-ccccccccccc2',
 '2ede13b1-b676-4333-9e8f-d9b361c884d4',
 0,
 null),

-- PO：発注 / 発注内容確認
('ffffffff-ffff-ffff-ffff-ffffffffff01',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
 'e1ed196d-4f90-45ca-8f07-7fb4ba55ea62',
 0,
 null),

-- PO：発注 / 購買責任者承認
('ffffffff-ffff-ffff-ffff-ffffffffff02',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2',
 '2ede13b1-b676-4333-9e8f-d9b361c884d4',
 0,
 null),

-- PO：発注 / 最終発注承認
-- 300,000円未満でも山田は必須
('ffffffff-ffff-ffff-ffff-ffffffffff03',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3',
 '2ede13b1-b676-4333-9e8f-d9b361c884d4',
 0,
 null),

-- 300,000円以上は佐藤も追加
('ffffffff-ffff-ffff-ffff-ffffffffff04',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3',
 'e1ed196d-4f90-45ca-8f07-7fb4ba55ea62',
 300000,
 null);