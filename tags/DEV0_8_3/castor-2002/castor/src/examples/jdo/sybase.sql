drop table prod
go
create table prod (
  id        int not null,
  name      varchar(200) not null,
  price     float not null,
  group_id  int not null
)
go
create unique index prod_pk on prod ( id )
go

drop table prod_group
go
create table prod_group (
  id    int not null,
  name  varchar(200) not null
)
go
create unique index prod_group_pk on prod_group ( id )
go

drop table prod_detail
go
create table prod_detail (
  id      int not null,
  prod_id int not null,
  name    varchar(200) not null
)
go
create unique index prod_detail_pk on prod_detail ( id )
go

drop table computer
go
create table computer (
  id   int not null,
  cpu  varchar(200) not null
)
go
create unique index computer_pk on computer ( id )
go

drop table category
go
create table category (
  id   int not null,
  name varchar(200) not null
)
go
create unique index category_pk on category ( id )
go

drop table category_prod
go
create table category_prod (
  prod_id   int not null,
  category_id   int not null
)
go
create unique index category_prod_pk on category_prod ( prod_id, category_id )
go




