-- purchase_order サンプルデータ
-- PR由来: 5件 / 直接作成: 1件
-- TODO: 合計金額は後程修正

TRUNCATE TABLE purchase_request_purchase_order, purchase_order RESTART IDENTITY;

INSERT INTO purchase_order (
    po_id,
    purchaser_user_id,
    due_date,
    supplier_id,
    snap_supplier_name,
    total_amount,
    status,
    current_step_order,
    note
) VALUES
-- PR由来: PENDING
-- 納期未定・低額。PRからそのままPO化されたが、まだ承認待ちのイメージ。
(
    '6f3b9242-2d1e-4e7a-b875-021c3f9a1a01',
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    NULL,
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    3200,
    'PENDING',
    1,
    NULL
),
-- PR由来: COMPLETED
-- 中額・納期過去。入荷/処理完了済みのイメージ。
(
    '3d062413-54a0-40f0-b764-3d1c6e4f5a02',
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    DATE '2026-04-25',
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    48600,
    'COMPLETED',
    3,
    NULL
),
-- PR由来: APPROVED
-- 30万円以上。PO承認フローの高額パターン確認用。
(
    'f7e34df9-f4cb-4c88-82c2-1fdfc7dd8a03',
    '169f1e17-619f-45bf-b6dc-8faed08c404c',
    DATE '2026-07-10',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    365000,
    'APPROVED',
    3,
    NULL
),
-- PR由来: REJECTED
-- 高めの金額。見積再確認などで否認されたイメージ。
(
    'b2e13f44-6f31-4c3a-a52a-2e6c0d9d8a04',
    '6fe99043-cbd1-49c0-96d4-c156c58a8e60',
    DATE '2026-07-20',
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    182400,
    'REJECTED',
    1,
    'PRから作成。設備部材の発注。見積条件の再確認が必要なため否認。'
),
-- PR由来: CANCELLED
-- 低〜中額。発注前に不要になってキャンセルされたイメージ。
(
    '0cc6a4f1-9f64-4a32-90fb-5ff8f4e12a05',
    '169f1e17-619f-45bf-b6dc-8faed08c404c',
    DATE '2026-08-05',
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    27800,
    'CANCELLED',
    1,
    'PRから作成。代替品を利用することになったためキャンセル。'
),
-- 直接作成: PENDING。purchase_request_purchase_order には登録しない。
-- PRなしで直接発注。中額・未来納期。
(
    '98f8c08b-4c55-49fd-b3a1-0a10fdc37a06',
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    DATE '2026-06-30',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    94600,
    'PENDING',
    1,
    '直接作成。PRを経由しない緊急手配品の発注。'
);
