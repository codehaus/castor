create table prod (
  id        int not null,
  name      varchar(200) not null,
  price     float not null,
  group_id  int not null
);

create unique index prod_pk on prod ( id );


create table prod_group (
  id    int not null,
  name  varchar(200) not null
);

create unique index prod_group_pk on prod_group ( id );


create table prod_inv (
  prod_id  int not null,
  quant    int not null
);

create unique index prod_inv_pk on prod_inv ( prod_id );


create table prod_detail (
  detail_id  int not null,
  name       varchar(200) not null,
  available  char(1) not null
);

create unique index prod_detail_pk on prod_detail ( detail_id );


create table prod_detail_rel (
  prod_id    int not null,
  detail_id  int not null
);

create unique index prod_detail_rel_pk on prod_detail_rel ( prod_id, detail_id );


create table computer (
  id   int not null,
  cpu  varchar(200) not null
);

create unique index computer_pk on computer ( id );




