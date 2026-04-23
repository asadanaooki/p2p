TRUNCATE TABLE purchase_request RESTART IDENTITY;

INSERT INTO purchase_request (
    supplier_id,
    requester_user_id,
    due_date,
    total_amount,
    status
) VALUES
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '2ede13b1-b676-4333-9e8f-d9b361c884d4',
    NULL,
    650,
    'PENDING'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '44140a4d-66b0-46e6-b26e-567fd52bea87',
    DATE '2026-04-25',
    7800,
    'COMPLETED'
),
(
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '6ad11f68-24d4-40ad-92a8-2d5855ba8235',
    DATE '2026-05-10',
    24800,
    'APPROVED'
),
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    'e1ed196d-4f90-45ca-8f07-7fb4ba55ea62',
    DATE '2026-05-20',
    56300,
    'REJECTED'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    'e44e195d-ca77-4c8f-87a1-7c67fe938c53',
    DATE '2026-06-01',
    128000,
    'CANCELLED'
);