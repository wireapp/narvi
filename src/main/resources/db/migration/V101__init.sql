create type issue_tracker as enum ('GITHUB');

create table issues
(
    narvi_id           serial primary key,
    issue_id           varchar(256)  not null,
    issue_tracker      issue_tracker not null,
    tracker_repository varchar(256)  not null,
    conversation_id    varchar(36)   not null,

    unique (issue_id, tracker_repository, issue_tracker)
);
