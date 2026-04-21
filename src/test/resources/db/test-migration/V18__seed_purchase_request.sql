TRUNCATE TABLE purchase_request RESTART IDENTITY;

INSERT INTO purchase_request (
    supplier_id,
    requester_user_id,
    total_amount,
    status
) VALUES
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '169f1e17-619f-45bf-b6dc-8faed08c404c',
    650,
    'PENDING'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    7800,
    'COMPLETED'
),
(
    'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f',
    '6fe99043-cbd1-49c0-96d4-c156c58a8e60',
    24800,
    'APPROVED'
),
(
    'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a',
    '169f1e17-619f-45bf-b6dc-8faed08c404c',
    56300,
    'REJECTED'
),
(
    'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c',
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    128000,
    'CANCELLED'
);