create table unanswered_prompt_log
(
    id              bigint  not null,
    created_date    timestamp(6),
    is_acknowledged boolean not null,
    prompt          TEXT    not null,
    user_id         bigint  not null,
    primary key (id)
);
comment
on table unanswered_prompt_log is 'Unanswered Prompt Log';
        comment
on column unanswered_prompt_log.id is 'Primary Key';

comment
on column unanswered_prompt_log.created_date is 'Timestamp when this record was created';
comment
on column unanswered_prompt_log.is_acknowledged is 'Has the admin received this? (false=no, true=received)';
        comment
on column unanswered_prompt_log.prompt is 'A question not answered in Vector DB.';
comment
on column unanswered_prompt_log.user_id is 'The ID of the user who asked the question.';