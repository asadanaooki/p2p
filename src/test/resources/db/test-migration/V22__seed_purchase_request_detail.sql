delete from purchase_request_detail;

insert into purchase_request_detail (
    pr_id,
    pr_detail_id,
    item_id,
    snap_item_name,
    snap_kind,
    unit_id,
    snap_unit_name,
    supplier_id,
    snap_supplier_name,
    quantity,
    snap_unit_price,
    subtotal_excluding_tax
) values
-- PR1: モノ明細1つ
(
    '88bfbcf6-2be6-4d31-8a46-155a7b58ab93',
    '33aaccfe-c7c4-4e37-ab48-259dbe7a7fdf',
    'a5c1b32a-7b01-49d3-8fef-48e0f39dc31f',
    'A4コピー用紙 500枚',
    'GOODS',
    '22222222-2222-2222-2222-222222222221',
    '個',
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    10,
    680,
    10 * 680
),

-- PR2: サービス明細1つ
(
    '3a5b130e-0279-4bca-bee3-d41cddbc9192',
    '21f9f4a4-7437-4b6b-9917-67be30c41e31',
    'd21fb363-afb3-4911-a051-fac108a06658',
    '会議室プロジェクター設置作業',
    'SERVICE',
    '22222222-2222-2222-2222-222222222224',
    '式',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    1,
    25000,
    1 * 25000
),

-- PR3: モノ明細3つ
(
    '3c4f62bf-855b-4c35-b19d-eb06acb16896',
    'fae1963c-2d5b-450a-8b12-4be480e65ecd',
    'a5c1b32a-7b01-49d3-8fef-48e0f39dc31f',
    'A4コピー用紙 500枚',
    'GOODS',
    '22222222-2222-2222-2222-222222222221',
    '個',
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    5,
    680,
    5 * 680
),
(
    '3c4f62bf-855b-4c35-b19d-eb06acb16896',
    '72a9f12f-bb87-4604-93f6-5d866542b0d8',
    '1bd0d872-69b1-4999-b522-ac202c481662',
    '油性ボールペン 黒 10本セット',
    'GOODS',
    '22222222-2222-2222-2222-222222222221',
    '個',
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    3,
    980,
    3 * 980
),
(
    '3c4f62bf-855b-4c35-b19d-eb06acb16896',
    'ad3e8b17-092a-4c73-acc4-1b799a5f5e97',
    'f758e462-f526-4b23-a822-8821c5c62adf',
    '24インチ液晶モニター',
    'GOODS',
    '22222222-2222-2222-2222-222222222222',
    '台',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    2,
    16800,
    2 * 16800
),

-- PR4: モノ1 + サービス1
(
    '56856dfe-8e7a-4524-9d05-9e161c6b8fc3',
    '41f50245-3273-4a2d-8afc-0cf6e0e8d2f1',
    'f758e462-f526-4b23-a822-8821c5c62adf',
    '24インチ液晶モニター',
    'GOODS',
    '22222222-2222-2222-2222-222222222222',
    '台',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    1,
    16800,
    1 * 16800
),
(
    '56856dfe-8e7a-4524-9d05-9e161c6b8fc3',
    '42a23476-55c0-4efe-a711-c546b553c2d2',
    '2cc30fd9-9dae-4abe-a145-b280d9de2f38',
    'プリンター保守サポート',
    'SERVICE',
    '22222222-2222-2222-2222-222222222225',
    '時間',
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    4,
    4500,
    4 * 4500
),

-- PR5: サービス明細2つ
(
    '6b2c5959-233f-4b54-8a9b-98f4a1b13c40',
    'ebe68ea0-daaf-41ff-99e3-8ab109c20eae',
    'd21fb363-afb3-4911-a051-fac108a06658',
    '会議室プロジェクター設置作業',
    'SERVICE',
    '22222222-2222-2222-2222-222222222224',
    '式',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    2,
    25000,
    2 * 25000
),
(
    '6b2c5959-233f-4b54-8a9b-98f4a1b13c40',
    '4d57ee8a-4dc6-4155-9f4b-9ee7985d4e21',
    '2cc30fd9-9dae-4abe-a145-b280d9de2f38',
    'プリンター保守サポート',
    'SERVICE',
    '22222222-2222-2222-2222-222222222225',
    '時間',
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    6,
    4500,
    6 * 4500
);

-- PRヘッダーの合計金額も明細合計に合わせる場合
update purchase_request pr
set total_amount_excluding_tax = detail.total
from (
  select
    pr_id,
    sum(subtotal_excluding_tax) as total
  from purchase_request_detail
  group by pr_id
) detail
where pr.pr_id = detail.pr_id;
