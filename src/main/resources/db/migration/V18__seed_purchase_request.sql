TRUNCATE TABLE purchase_request RESTART IDENTITY;

INSERT INTO purchase_request (
    supplier_id,
    pr_id,
    requester_user_id,
    due_date,
    total_amount,
    status
) VALUES
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '7ff6c8e6-4978-45c9-8ae0-2751ee54770a',
    '2ede13b1-b676-4333-9e8f-d9b361c884d4',
    NULL,
    650,
    'PENDING'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '085c49a9-8842-464d-a7a1-f4907732c01d',
    '2ede13b1-b676-4333-9e8f-d9b361c884d4',
    DATE '2026-04-25',
    7800,
    'COMPLETED'
),
(
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    'c24303dd-c257-41fd-b226-689c6369b0c9',
    '6ad11f68-24d4-40ad-92a8-2d5855ba8235',
    DATE '2026-05-10',
    24800,
    'APPROVED'
),
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '5690f8f2-454c-4d33-beb7-d15a49bae6d3',
    'e1ed196d-4f90-45ca-8f07-7fb4ba55ea62',
    DATE '2026-05-20',
    56300,
    'REJECTED'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '761293c2-6103-4006-9a4b-4e9e3ceb3001',
    '6ad11f68-24d4-40ad-92a8-2d5855ba8235',
    DATE '2026-06-01',
    128000,
    'CANCELLED'
);
