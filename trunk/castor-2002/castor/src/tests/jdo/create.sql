create table test_table (
  id int not null,
  value1 varchar(200) not null,
  value2 varchar(200)
);

create unique index test_table_pk on test_table ( id );


create table test_master (
  id       int not null,
  value    varchar(200) not null,
  group_id int not null
);

create unique index test_master_pk on test_master ( id );

create table test_detail (
  detail_id  int not null,
  master_id  int not null,
  value      varchar(200) not null
);

create unique index test_detail_pk on test_detail ( detail_id );

create table test_group (
  id      int not null,
  value   varchar(200) not null
);

create unique index test_group_pk on test_group ( id );

create table test_many (
  id      int not null,
  value   varchar(200) not null
);

create unique index test_many_pk on test_many ( id );

create table test_many_rel (
  master_id  int not null,
  many_id    int not null
);

create unique index test_many_rel_pk on test_many_rel ( master_id, many_id );


create table test_keygen (
  id int not null,
  attr varchar(200) not null,
);

create unique index test_keygen_pk on test_keygen ( id );

create table test_keygen_ext (
  id int not null,
  ext varchar(200) not null,
);

create unique index test_keygen_ext_pk on test_keygen_ext ( id );

create table test_seqtable (
  table_name varchar(200) not null,
  max_id int null,
);

create unique index test_seqtable_pk on test_seqtable ( table_name );

