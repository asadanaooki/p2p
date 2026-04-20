insert into users (
    user_id,
    last_name,
    first_name,
    last_name_kana,
    first_name_kana,
    email,
    password_hash,
    role_id,
    is_active
) values (
    '36a1d5d9-15b8-45d5-8ae7-607244bbe36e',
    '山田',
    '太郎',
    'ヤマダ',
    'タロウ',
    'siotan0926@gmail.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '95daf9ce-b599-41e0-ae0d-f4687e718a2c',
    true
), (
    '169f1e17-619f-45bf-b6dc-8faed08c404c',
    '佐藤',
    '花子',
    'サトウ',
    'ハナコ',
    'sato.hanako@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '6862542a-1954-4192-81e8-f18c583ade01',
    true
), (
    '6fe99043-cbd1-49c0-96d4-c156c58a8e60',
    '鈴木',
    '一郎',
    'スズキ',
    'イチロウ',
    'suzuki.ichiro@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '71dc166d-5059-4fbc-8bca-71d0c5dc2526',
    true
);