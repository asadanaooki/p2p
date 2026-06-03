alter table approval_step drop constraint approval_step_approval_workflow_id_fkey;
alter table approval_step_approver drop constraint approval_step_approver_approval_step_id_fkey;

alter table approval_step add foreign key (approval_workflow_id)
  references approval_workflow (approval_workflow_id) on delete cascade;
alter table approval_step_approver add foreign key (approval_step_id)
  references approval_step (approval_step_id) on delete cascade;