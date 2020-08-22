create type issue_tracker as enum ('GITHUB');

create table issues
(
    issue_id        varchar(256)  not null,
    issue_tracker   issue_tracker not null,
    conversation_id varchar(36)   not null,

    unique (issue_id, issue_tracker),
    primary key (issue_id, issue_tracker, conversation_id)
);

create table user_names
(
    wire_user_name   varchar(256)  not null,
    tracker_username varchar(256)  not null,
    issue_tracker    issue_tracker not null,

    primary key (wire_user_name, issue_tracker)
)
