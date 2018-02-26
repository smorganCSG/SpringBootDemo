create table speaker
(
  id serial
    constraint pk_speaker_id
    primary key,
  firstname varchar(255),
  lastname varchar(255),
  bio varchar(255)
);

insert into speaker (firstname, lastname) values ('Michelle', 'Morgan');
insert into speaker (firstname, lastname) values ('Scott', 'Morgan');
insert into speaker (firstname, lastname) values ('Rowan', 'Morgan');
insert into speaker (firstname, lastname) values ('Georgia', 'Morgan');
insert into speaker (firstname, lastname) values ('Aaron', 'Morgan');

