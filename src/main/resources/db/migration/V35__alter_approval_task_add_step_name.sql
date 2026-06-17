alter table approval_task add column step_name varchar(100);
  
UPDATE approval_task SET step_name = '1段階目承認';

ALTER TABLE approval_task ALTER COLUMN step_name SET NOT NULL;