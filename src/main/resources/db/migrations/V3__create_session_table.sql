create table conference_session
(
  id serial
    constraint pk_conference_session_id
    primary key,
  name varchar(255),
  track varchar(255)
);
