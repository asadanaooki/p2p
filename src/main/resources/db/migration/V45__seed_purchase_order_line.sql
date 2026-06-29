-- purchase_order / purchase_request_purchase_order / purchase_order_line / approval_task サンプルデータ再作成
--
-- 方針:
-- - STANDARD は GOODS 明細を purchase_request + supplier 単位でまとめて発注する
-- - SERVICE は期間が明細ごとに異なる前提のため、purchase_request_detail 1件につき purchase_order 1件に分割する
-- - STANDARD と SERVICE は同じPR・同じサプライヤーでも別発注にする
-- - purchase_order.total_amount_excluding_tax は STANDARD は対象GOODS明細合計、SERVICE は対象SERVICE明細1件の金額で作成する
-- - 作成後、purchase_order.total_amount_excluding_tax は purchase_order_line の合計で再確定する
-- - PRを経由しない直接作成の SERVICE 発注も1件残す
-- - PR / PO の approval_task をステータスに合わせて再作成する
-- - CANCELLED は approval_task.status に該当値がないため承認タスクを作成しない

DELETE FROM approval_task WHERE document_type IN ('PR', 'PO');

TRUNCATE TABLE purchase_request_purchase_order, purchase_order_line, purchase_order RESTART IDENTITY;

DROP TABLE IF EXISTS tmp_purchase_order_line_seed;
DROP TABLE IF EXISTS tmp_purchase_order_seed;

CREATE TEMP TABLE tmp_purchase_order_seed (
    display_order integer not null,
    po_id char(36) not null,
    pr_id char(36) not null,
    -- SERVICEは期間が明細ごとに異なるため、どのPR明細から作ったPOかを固定する。
    -- STANDARDは同一PR・同一サプライヤーのGOODS明細をまとめるためNULLにする。
    pr_detail_id char(36),
    supplier_id char(36) not null,
    snap_supplier_name varchar(100) not null,
    order_type varchar(10) not null,
    delivery_due_date date,
    service_period_from date,
    service_period_to date,
    current_step_order integer not null,
    note varchar(500),
    constraint tmp_purchase_order_seed_po_pk primary key (po_id),
    constraint tmp_purchase_order_seed_display_order_uniq unique (display_order),
    -- SERVICEは1つのPR明細から1つのPOを作るため、同じPR明細を複数POに使わせない。
    -- PostgreSQLのUNIQUEはNULLを複数許可するため、STANDARDのNULLは複数行あってよい。
    constraint tmp_purchase_order_seed_service_detail_uniq unique (pr_detail_id),
    constraint tmp_purchase_order_seed_order_type_check check (
        (
            order_type = 'STANDARD'
            and pr_detail_id is null
            and delivery_due_date is not null
            and service_period_from is null
            and service_period_to is null
        )
        or
        (
            order_type = 'SERVICE'
            and pr_detail_id is not null
            and delivery_due_date is null
            and service_period_from is not null
            and service_period_to is not null
            and service_period_from <= service_period_to
        )
    )
);

INSERT INTO tmp_purchase_order_seed (
    display_order,
    po_id,
    pr_id,
    pr_detail_id,
    supplier_id,
    snap_supplier_name,
    order_type,
    delivery_due_date,
    service_period_from,
    service_period_to,
    current_step_order,
    note
) VALUES
-- PR1: GOODS / 神奈川文具 => STANDARD
(
    1,
    '6f3b9242-2d1e-4e7a-b875-021c3f9a1a01',
    '7ff6c8e6-4978-45c9-8ae0-2751ee54770a',
    NULL,
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    'STANDARD',
    DATE '2026-06-25',
    NULL,
    NULL,
    1,
    'PRから作成。神奈川文具の標準発注。PRの納期未定分はサンプル表示用に納期を設定。'
),
-- PR2: SERVICE / 関西オフィスサービス => SERVICE。サービスは1明細1PO。
(
    2,
    '3d062413-54a0-40f0-b764-3d1c6e4f5a02',
    '085c49a9-8842-464d-a7a1-f4907732c01d',
    '20000000-0000-4000-8000-000000000001',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    'SERVICE',
    NULL,
    DATE '2026-04-25',
    DATE '2026-04-25',
    3,
    'PRから作成。関西オフィスサービスのサービス発注。サービス期間が明細ごとに異なるため1明細1PO。'
),
-- PR3: GOODS / 神奈川文具 => STANDARD
(
    3,
    'f7e34df9-f4cb-4c88-82c2-1fdfc7dd8a03',
    'c24303dd-c257-41fd-b226-689c6369b0c9',
    NULL,
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '神奈川文具株式会社',
    'STANDARD',
    DATE '2026-05-10',
    NULL,
    NULL,
    3,
    'PRから作成。同一PR内の神奈川文具分を標準発注として分割。'
),
-- PR3: GOODS / 関西オフィスサービス => STANDARD
(
    4,
    'a41f6d90-9459-4f0c-a8d7-5c4e2b8d7004',
    'c24303dd-c257-41fd-b226-689c6369b0c9',
    NULL,
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    'STANDARD',
    DATE '2026-05-10',
    NULL,
    NULL,
    3,
    'PRから作成。同一PR内の関西オフィスサービス分を標準発注として分割。'
),
-- PR4: GOODS / 関西オフィスサービス => STANDARD
(
    5,
    'b2e13f44-6f31-4c3a-a52a-2e6c0d9d8a04',
    '5690f8f2-454c-4d33-beb7-d15a49bae6d3',
    NULL,
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    'STANDARD',
    DATE '2026-05-20',
    NULL,
    NULL,
    1,
    'PRから作成。見積条件の再確認が必要なため否認。'
),
-- PR4: SERVICE / 中部設備サプライ => SERVICE。サービスは1明細1PO。
(
    6,
    'd8b3f0c1-5a64-447a-8b29-2d88b8e2f005',
    '5690f8f2-454c-4d33-beb7-d15a49bae6d3',
    '40000000-0000-4000-8000-000000000002',
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    'SERVICE',
    NULL,
    DATE '2026-05-01',
    DATE '2026-05-31',
    1,
    'PRから作成。中部設備サプライのサービス発注。サービス期間が明細ごとに異なるため1明細1PO。見積条件の再確認が必要なため否認。'
),
-- PR5: SERVICE / 関西オフィスサービス => SERVICE。サービスは1明細1PO。
(
    7,
    '0cc6a4f1-9f64-4a32-90fb-5ff8f4e12a05',
    '761293c2-6103-4006-9a4b-4e9e3ceb3001',
    '50000000-0000-4000-8000-000000000001',
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    'SERVICE',
    NULL,
    DATE '2026-06-01',
    DATE '2026-06-01',
    1,
    'PRから作成。関西オフィスサービスのサービス発注。サービス期間が明細ごとに異なるため1明細1PO。代替対応になったためキャンセル。'
),
-- PR5: SERVICE / 中部設備サプライ => SERVICE。サービスは1明細1PO。
(
    8,
    'e4c9f1a2-7b83-41d0-9a65-3c2d5f7a8006',
    '761293c2-6103-4006-9a4b-4e9e3ceb3001',
    '50000000-0000-4000-8000-000000000002',
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '中部設備サプライ株式会社',
    'SERVICE',
    NULL,
    DATE '2026-06-01',
    DATE '2026-06-30',
    1,
    'PRから作成。中部設備サプライのサービス発注。サービス期間が明細ごとに異なるため1明細1PO。代替対応になったためキャンセル。'
);

WITH po_amount_from_detail AS (
    -- STANDARD は同一PR・同一サプライヤーのGOODS明細を1POにまとめるため、ここだけSUMする。
    SELECT
        seed.po_id,
        SUM(prd.subtotal_excluding_tax)::integer AS total_amount_excluding_tax
    FROM tmp_purchase_order_seed seed
    JOIN purchase_request_detail prd
      ON prd.pr_id = seed.pr_id
     AND prd.supplier_id = seed.supplier_id
     AND prd.snap_kind = 'GOODS'
    WHERE seed.order_type = 'STANDARD'
      AND seed.pr_detail_id IS NULL
    GROUP BY seed.po_id

    UNION ALL

    -- SERVICE は期間が明細ごとに異なる前提のため、SUMしない。
    -- seed.pr_detail_id で対象PR明細を1件に固定し、その明細金額をそのままPO金額にする。
    SELECT
        seed.po_id,
        prd.subtotal_excluding_tax::integer AS total_amount_excluding_tax
    FROM tmp_purchase_order_seed seed
    JOIN purchase_request_detail prd
      ON prd.pr_detail_id = seed.pr_detail_id
     AND prd.pr_id = seed.pr_id
     AND prd.supplier_id = seed.supplier_id
     AND prd.snap_kind = 'SERVICE'
    WHERE seed.order_type = 'SERVICE'
)
INSERT INTO purchase_order (
    po_id,
    order_type,
    purchaser_user_id,
    delivery_due_date,
    supplier_id,
    snap_supplier_name,
    total_amount_excluding_tax,
    status,
    current_step_order,
    note,
    service_period_from,
    service_period_to
)
SELECT
    seed.po_id,
    seed.order_type,
    pr.requester_user_id,
    seed.delivery_due_date,
    seed.supplier_id,
    seed.snap_supplier_name,
    po_amount_from_detail.total_amount_excluding_tax,
    pr.status,
    seed.current_step_order,
    seed.note,
    seed.service_period_from,
    seed.service_period_to
FROM tmp_purchase_order_seed seed
JOIN purchase_request pr
  ON pr.pr_id = seed.pr_id
JOIN po_amount_from_detail
  ON po_amount_from_detail.po_id = seed.po_id
ORDER BY seed.display_order;

-- PRを経由しない直接作成のサービス発注。
-- 直接作成もSERVICEなので、1POに1明細のみ持たせる。
INSERT INTO purchase_order (
    po_id,
    order_type,
    purchaser_user_id,
    delivery_due_date,
    supplier_id,
    snap_supplier_name,
    total_amount_excluding_tax,
    status,
    current_step_order,
    note,
    service_period_from,
    service_period_to
) VALUES (
    '98f8c08b-4c55-49fd-b3a1-0a10fdc37a06',
    'SERVICE',
    '2ede13b1-b676-4333-9e8f-d9b361c884d4',
    NULL,
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '関西オフィスサービス株式会社',
    94600,
    'PENDING',
    1,
    '直接作成。PRを経由しない緊急対応サービスの発注。',
    DATE '2025-03-02',
    DATE '2026-06-22'
);

INSERT INTO purchase_request_purchase_order (
    pr_id,
    po_id
)
SELECT
    pr_id,
    po_id
FROM tmp_purchase_order_seed
ORDER BY display_order;

CREATE TEMP TABLE tmp_purchase_order_line_seed (
    po_line_id char(36) not null,
    pr_detail_id char(36) not null,
    constraint tmp_purchase_order_line_seed_pk primary key (po_line_id),
    constraint tmp_purchase_order_line_seed_pr_detail_uniq unique (pr_detail_id)
);

INSERT INTO tmp_purchase_order_line_seed (
    po_line_id,
    pr_detail_id
) VALUES
    ('90000000-0000-4000-8000-000000000001', '10000000-0000-4000-8000-000000000001'),
    ('90000000-0000-4000-8000-000000000002', '20000000-0000-4000-8000-000000000001'),
    ('90000000-0000-4000-8000-000000000003', '30000000-0000-4000-8000-000000000001'),
    ('90000000-0000-4000-8000-000000000004', '30000000-0000-4000-8000-000000000002'),
    ('90000000-0000-4000-8000-000000000005', '30000000-0000-4000-8000-000000000003'),
    ('90000000-0000-4000-8000-000000000006', '40000000-0000-4000-8000-000000000001'),
    ('90000000-0000-4000-8000-000000000007', '40000000-0000-4000-8000-000000000002'),
    ('90000000-0000-4000-8000-000000000008', '50000000-0000-4000-8000-000000000001'),
    ('90000000-0000-4000-8000-000000000009', '50000000-0000-4000-8000-000000000002');

WITH pr_lines AS (
    SELECT
        line_seed.po_line_id,
        seed.po_id,
        (ROW_NUMBER() OVER (
            PARTITION BY seed.po_id
            ORDER BY prd.line_no, prd.pr_detail_id
        ))::integer AS line_no,
        prd.item_id,
        CASE
            WHEN seed.order_type = 'STANDARD' THEN prd.unit_id
            ELSE NULL
        END AS unit_id,
        prd.snap_item_name,
        prd.subtotal_excluding_tax AS line_amount_excluding_tax,
        CASE
            WHEN seed.order_type = 'STANDARD' THEN prd.snap_unit_name
            ELSE NULL
        END AS snap_unit_name,
        CASE
            WHEN seed.order_type = 'STANDARD' THEN prd.snap_unit_price
            ELSE NULL
        END AS snap_unit_price,
        CASE
            WHEN seed.order_type = 'STANDARD' THEN prd.quantity
            ELSE NULL
        END AS quantity
    FROM tmp_purchase_order_line_seed line_seed
    JOIN purchase_request_detail prd
      ON prd.pr_detail_id = line_seed.pr_detail_id
    JOIN tmp_purchase_order_seed seed
      ON seed.pr_id = prd.pr_id
     AND seed.supplier_id = prd.supplier_id
     AND (
            (
                seed.order_type = 'STANDARD'
                AND seed.pr_detail_id IS NULL
                AND prd.snap_kind <> 'SERVICE'
            )
            OR
            (
                seed.order_type = 'SERVICE'
                AND seed.pr_detail_id = prd.pr_detail_id
                AND prd.snap_kind = 'SERVICE'
            )
         )
)
INSERT INTO purchase_order_line (
    po_line_id,
    po_id,
    line_no,
    item_id,
    unit_id,
    snap_item_name,
    line_amount_excluding_tax,
    snap_unit_name,
    snap_unit_price,
    quantity
)
SELECT
    po_line_id,
    po_id,
    line_no,
    item_id,
    unit_id,
    snap_item_name,
    line_amount_excluding_tax,
    snap_unit_name,
    snap_unit_price,
    quantity
FROM pr_lines
ORDER BY po_id, line_no;

-- 直接作成POの明細。PR由来ではないため item_id / unit_id は持たせない。
INSERT INTO purchase_order_line (
    po_line_id,
    po_id,
    line_no,
    item_id,
    unit_id,
    snap_item_name,
    line_amount_excluding_tax,
    snap_unit_name,
    snap_unit_price,
    quantity
) VALUES (
    '90000000-0000-4000-8000-000000000010',
    '98f8c08b-4c55-49fd-b3a1-0a10fdc37a06',
    1,
    NULL,
    NULL,
    '緊急対応サービス',
    94600,
    NULL,
    NULL,
    NULL
);

-- POヘッダーの合計金額を明細合計で確定する。
UPDATE purchase_order po
SET total_amount_excluding_tax = line_sum.total_amount_excluding_tax
FROM (
    SELECT
        po_id,
        SUM(line_amount_excluding_tax)::integer AS total_amount_excluding_tax
    FROM purchase_order_line
    GROUP BY po_id
) line_sum
WHERE po.po_id = line_sum.po_id;

-- PR / PO承認タスクを作成する。
-- 承認者とステップ名は approval_workflow / approval_step / approval_step_approver から取得する。
-- PENDING  : 現在ステップまで作成し、前段階があればAPPROVED、現在ステップはPENDINGにする。
-- APPROVED : current_step_order までAPPROVEDにする。
-- COMPLETED: current_step_order までAPPROVEDにする。承認後の後続処理まで完了した状態。
-- REJECTED : 現在ステップまで作成し、前段階があればAPPROVED、現在ステップはREJECTEDにする。
-- CANCELLED: approval_task.statusにCANCELLEDがないため、承認タスクを作成しない。
WITH approval_target AS (
    SELECT
        pr.pr_id AS document_id,
        'PR'::varchar(10) AS document_type,
        pr.display_number,
        pr.total_amount_excluding_tax,
        pr.status AS document_status,
        pr.current_step_order
    FROM purchase_request pr
    WHERE pr.status <> 'CANCELLED'

    UNION ALL

    SELECT
        po.po_id AS document_id,
        'PO'::varchar(10) AS document_type,
        po.display_number,
        po.total_amount_excluding_tax,
        po.status AS document_status,
        po.current_step_order
    FROM purchase_order po
    WHERE po.status <> 'CANCELLED'
), task_base AS (
    SELECT
        target.document_id,
        target.document_type,
        target.display_number,
        target.document_status,
        target.current_step_order,
        step.step_order,
        approver.user_id,
        step.name AS step_name,
        CASE
            WHEN target.document_status = 'PENDING'
             AND step.step_order < target.current_step_order THEN 'APPROVED'
            WHEN target.document_status = 'PENDING'
             AND step.step_order = target.current_step_order THEN 'PENDING'
            WHEN target.document_status IN ('APPROVED', 'COMPLETED')
             AND step.step_order <= target.current_step_order THEN 'APPROVED'
            WHEN target.document_status = 'REJECTED'
             AND step.step_order < target.current_step_order THEN 'APPROVED'
            WHEN target.document_status = 'REJECTED'
             AND step.step_order = target.current_step_order THEN 'REJECTED'
        END::varchar(10) AS task_status
    FROM approval_target target
    JOIN approval_workflow workflow
      ON workflow.document_type = target.document_type
    JOIN approval_step step
      ON step.approval_workflow_id = workflow.approval_workflow_id
    JOIN approval_step_approver approver
      ON approver.approval_step_id = step.approval_step_id
     AND target.total_amount_excluding_tax >= approver.amount_min
     AND (
            approver.amount_max IS NULL
         OR target.total_amount_excluding_tax <= approver.amount_max
     )
    WHERE step.step_order <= target.current_step_order
)
INSERT INTO approval_task (
    document_id,
    step_order,
    user_id,
    document_type,
    status,
    comment,
    step_name,
    acted_at
)
SELECT
    document_id,
    step_order,
    user_id,
    document_type,
    task_status,
    CASE
        WHEN task_status = 'PENDING' THEN '承認待ち。'
        WHEN task_status = 'REJECTED' THEN '見積条件の再確認が必要なため否認。'
        WHEN document_status = 'COMPLETED' THEN '承認済み。後続処理まで完了。'
        ELSE '承認済み。'
    END AS comment,
    step_name,
    CASE
        WHEN task_status = 'PENDING' THEN NULL
        ELSE TIMESTAMP '2026-06-01 09:00:00'
             + ((CASE WHEN document_type = 'PO' THEN 100 ELSE 0 END + display_number * 10 + step_order) * INTERVAL '1 hour')
    END AS acted_at
FROM task_base
WHERE task_status IS NOT NULL
ORDER BY document_type, document_id, step_order, user_id;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM tmp_purchase_order_seed seed
        WHERE seed.order_type = 'SERVICE'
          AND NOT EXISTS (
                SELECT 1
                FROM purchase_request_detail prd
                WHERE prd.pr_detail_id = seed.pr_detail_id
                  AND prd.pr_id = seed.pr_id
                  AND prd.supplier_id = seed.supplier_id
                  AND prd.snap_kind = 'SERVICE'
          )
    ) THEN
        RAISE EXCEPTION 'service purchase_order seed must map to exactly one SERVICE purchase_request_detail';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM tmp_purchase_order_seed seed
        LEFT JOIN purchase_request_purchase_order prpo
          ON prpo.po_id = seed.po_id
         AND prpo.pr_id = seed.pr_id
        WHERE seed.order_type = 'SERVICE'
        GROUP BY seed.po_id
        HAVING COUNT(prpo.pr_id) <> 1
    ) THEN
        RAISE EXCEPTION 'service purchase_order created from PR must link to exactly one purchase_request';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_order po
        JOIN (
            SELECT
                po_id,
                SUM(line_amount_excluding_tax)::integer AS total_amount_excluding_tax
            FROM purchase_order_line
            GROUP BY po_id
        ) line_sum
          ON line_sum.po_id = po.po_id
        WHERE po.total_amount_excluding_tax <> line_sum.total_amount_excluding_tax
    ) THEN
        RAISE EXCEPTION 'purchase_order.total_amount_excluding_tax does not match purchase_order_line sum';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_order po
        LEFT JOIN purchase_order_line pol
          ON pol.po_id = po.po_id
        WHERE pol.po_id IS NULL
    ) THEN
        RAISE EXCEPTION 'purchase_order without purchase_order_line exists';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_order po
        JOIN purchase_order_line pol
          ON pol.po_id = po.po_id
        WHERE po.order_type = 'SERVICE'
        GROUP BY po.po_id
        HAVING COUNT(*) <> 1
    ) THEN
        RAISE EXCEPTION 'service purchase_order must have exactly one purchase_order_line';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_order po
        JOIN purchase_order_line pol
          ON pol.po_id = po.po_id
        WHERE po.order_type = 'SERVICE'
          AND (
                pol.unit_id IS NOT NULL
             OR pol.snap_unit_name IS NOT NULL
             OR pol.snap_unit_price IS NOT NULL
             OR pol.quantity IS NOT NULL
          )
    ) THEN
        RAISE EXCEPTION 'service purchase_order_line must not have standard quantity/unit columns';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_request_detail prd
        LEFT JOIN tmp_purchase_order_line_seed line_seed
          ON line_seed.pr_detail_id = prd.pr_detail_id
        WHERE line_seed.pr_detail_id IS NULL
    ) THEN
        RAISE EXCEPTION 'purchase_request_detail without seeded purchase_order_line exists';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM tmp_purchase_order_line_seed
        GROUP BY pr_detail_id
        HAVING COUNT(*) <> 1
    ) THEN
        RAISE EXCEPTION 'purchase_request_detail must map to exactly one purchase_order_line';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM purchase_request pr
        LEFT JOIN (
            SELECT
                prpo.pr_id,
                SUM(pol.line_amount_excluding_tax)::integer AS total_amount_excluding_tax
            FROM purchase_request_purchase_order prpo
            JOIN purchase_order_line pol
              ON pol.po_id = prpo.po_id
            GROUP BY prpo.pr_id
        ) po_sum
          ON po_sum.pr_id = pr.pr_id
        WHERE po_sum.pr_id IS NULL
           OR pr.total_amount_excluding_tax <> po_sum.total_amount_excluding_tax
    ) THEN
        RAISE EXCEPTION 'purchase_request.total_amount_excluding_tax does not match related purchase_order_line sum';
    END IF;


    IF EXISTS (
        SELECT 1
        FROM approval_task task
        WHERE task.document_type = 'PR'
          AND NOT EXISTS (
                SELECT 1
                FROM purchase_request pr
                WHERE pr.pr_id = task.document_id
          )
    ) THEN
        RAISE EXCEPTION 'PR approval_task without purchase_request exists';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM approval_task task
        WHERE task.document_type = 'PO'
          AND NOT EXISTS (
                SELECT 1
                FROM purchase_order po
                WHERE po.po_id = task.document_id
          )
    ) THEN
        RAISE EXCEPTION 'PO approval_task without purchase_order exists';
    END IF;

    IF EXISTS (
        WITH approval_target AS (
            SELECT
                pr.pr_id AS document_id,
                'PR'::varchar(10) AS document_type,
                pr.display_number,
                pr.total_amount_excluding_tax,
                pr.status AS document_status,
                pr.current_step_order
            FROM purchase_request pr
            WHERE pr.status <> 'CANCELLED'

            UNION ALL

            SELECT
                po.po_id AS document_id,
                'PO'::varchar(10) AS document_type,
                po.display_number,
                po.total_amount_excluding_tax,
                po.status AS document_status,
                po.current_step_order
            FROM purchase_order po
            WHERE po.status <> 'CANCELLED'
        ), expected AS (
            SELECT
                target.document_id,
                target.document_type,
                step.step_order,
                approver.user_id,
                CASE
                    WHEN target.document_status = 'PENDING'
                     AND step.step_order < target.current_step_order THEN 'APPROVED'
                    WHEN target.document_status = 'PENDING'
                     AND step.step_order = target.current_step_order THEN 'PENDING'
                    WHEN target.document_status IN ('APPROVED', 'COMPLETED')
                     AND step.step_order <= target.current_step_order THEN 'APPROVED'
                    WHEN target.document_status = 'REJECTED'
                     AND step.step_order < target.current_step_order THEN 'APPROVED'
                    WHEN target.document_status = 'REJECTED'
                     AND step.step_order = target.current_step_order THEN 'REJECTED'
                END::varchar(10) AS status,
                step.name AS step_name
            FROM approval_target target
            JOIN approval_workflow workflow
              ON workflow.document_type = target.document_type
            JOIN approval_step step
              ON step.approval_workflow_id = workflow.approval_workflow_id
            JOIN approval_step_approver approver
              ON approver.approval_step_id = step.approval_step_id
             AND target.total_amount_excluding_tax >= approver.amount_min
             AND (
                    approver.amount_max IS NULL
                 OR target.total_amount_excluding_tax <= approver.amount_max
             )
            WHERE step.step_order <= target.current_step_order
        ), actual AS (
            SELECT
                task.document_id,
                task.document_type,
                task.step_order,
                task.user_id,
                task.status,
                task.step_name,
                task.acted_at
            FROM approval_task task
            WHERE task.document_type IN ('PR', 'PO')
        )
        SELECT 1
        FROM expected
        FULL OUTER JOIN actual
          ON actual.document_id = expected.document_id
         AND actual.document_type = expected.document_type
         AND actual.step_order = expected.step_order
         AND actual.user_id = expected.user_id
        WHERE expected.document_id IS NULL
           OR actual.document_id IS NULL
           OR expected.status <> actual.status
           OR expected.step_name <> actual.step_name
           OR (expected.status = 'PENDING' AND actual.acted_at IS NOT NULL)
           OR (expected.status <> 'PENDING' AND actual.acted_at IS NULL)
    ) THEN
        RAISE EXCEPTION 'approval_task does not match PR/PO approval state';
    END IF;
END;
$$;

DROP TABLE IF EXISTS tmp_purchase_order_line_seed;
DROP TABLE IF EXISTS tmp_purchase_order_seed;
