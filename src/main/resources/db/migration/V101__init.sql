create table templates
(
    id                 serial primary key,
    issue_tracker      varchar(256)        not null,
    tracker_repository varchar(256)        not null,
    trigger            varchar(256) unique not null,
    unique (issue_tracker, tracker_repository)
);


create table issues
(
    narvi_id        serial primary key,
    issue_id        varchar(256)                      not null,
    conversation_id varchar(36) unique                not null,
    template_id     integer references templates (id) not null
);
