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
    '4dfefacd-59dc-413c-bd75-b86c8e230c2f',
    true
), (
    '佐藤',
    '花子',
    'サトウ',
    'ハナコ',
    'sato.hanako@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '54ec2e48-8072-4249-aa39-6cc24492f174',
    true
), (
    '鈴木',
    '一郎',
    'スズキ',
    'イチロウ',
    'suzuki.ichiro@example.com',
    '$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu',
    '44140a4d-66b0-46e6-b26e-567fd52bea87',
    true
);