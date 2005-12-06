drop table if exists sorted_container;
create table sorted_container (
  id        int not null,
  name      varchar(200) not null
);

drop table if exists sorted_item;
create table sorted_item(
  id        int not null,
  id_1		int not null,
  name      varchar(200) not null
);

insert into sorted_container (id, name) values (1, 'entity11');
insert into sorted_container(id, name) values (2, 'entity12');
insert into sorted_container(id, name) values (1, 'entity13');

insert into sorted_item (id, id_1, name) values (1, 1, 'entity21');
insert into sorted_item (id, id_1, name) values (2, 1, 'entity22');
insert into sorted_item (id, id_1, name) values (1, 2, 'entity23');
