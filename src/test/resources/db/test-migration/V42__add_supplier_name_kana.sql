alter table supplier add column name_kana varchar(255);

UPDATE supplier
SET name_kana = 'カナガワブングカブシキガイシャ'
WHERE supplier_id = 'a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c';

UPDATE supplier
SET name_kana = 'カンサイオフィスサービスカブシキガイシャ'
WHERE supplier_id = 'b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f';

UPDATE supplier
SET name_kana = 'チュウブセツビサプライカブシキガイシャ'
WHERE supplier_id = 'c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a';

ALTER TABLE supplier ALTER COLUMN name_kana SET NOT NULL;
