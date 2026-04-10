insert into users (
    last_name,
    first_name,
    last_name_kana,
    first_name_kana,
    email,
    password_hash,
    role_id,
    is_active
) values (
    '山田',
    '太郎',
    'ヤマダ',
    'タロウ',
    'siotan0926@gmail.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '95daf9ce-b599-41e0-ae0d-f4687e718a2c',
    true
), (
    '佐藤',
    '花子',
    'サトウ',
    'ハナコ',
    'sato.hanako@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '6862542a-1954-4192-81e8-f18c583ade01',
    true
), (
    '鈴木',
    '一郎',
    'スズキ',
    'イチロウ',
    'suzuki.ichiro@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '71dc166d-5059-4fbc-8bca-71d0c5dc2526',
    true
);