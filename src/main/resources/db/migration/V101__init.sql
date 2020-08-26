create table issues
(
    narvi_id           serial primary key,
    issue_id           varchar(256) not null,
    issue_tracker      varchar(256) not null,
    tracker_repository varchar(256) not null,
    conversation_id    varchar(36)  not null,

    unique (issue_id, tracker_repository, issue_tracker)
);
