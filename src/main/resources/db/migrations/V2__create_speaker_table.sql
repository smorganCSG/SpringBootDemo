create table speaker
(
  id serial
    constraint pk_speaker_id
    primary key,
  firstname varchar(255),
  lastname varchar(255),
  bio varchar(255)
);



