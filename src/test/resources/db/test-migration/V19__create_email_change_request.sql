drop table if exists email_change_request;

create table email_change_request (
    user_id char(36) primary key,
    token_hash char(64) not null,
    new_email varchar(254) not null,
    expires_at timestamp not null,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    foreign key (user_id) references users(user_id)
);