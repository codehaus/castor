-- test_table
drop table test_table
go
create table test_table (
  id      int          not null,
  value1  varchar(200) not null,
  value2  varchar(200) null
)
go
create unique index test_table_pk on test_table ( id )
go
grant all on test_table to test
go


-- test_master
drop table test_master
go
create table test_master (
  id       int          not null,
  value    varchar(200) not null,
  group_id int          not null
)
go
create unique index test_master_pk on test_master ( id )
go
grant all on test_master to test
go


-- test_detail
drop table test_detail
go
create table test_detail (
  detail_id  int          not null,
  master_id  int          not null,
  value      varchar(200) not null
)
go
create unique index test_detail_pk on test_detail ( detail_id )
go
grant all on test_detail to test
go


-- test_group
drop table test_group
go
create table test_group (
  id     int          not null,
  value  varchar(200) not null
)
go
create unique index test_group_pk on test_group ( id )
go
grant all on test_group to test
go


-- test_types
drop table test_types
go
create table test_types (
  id     numeric       not null,
  tdt    datetime      not null,
  ttm    smalldatetime not null,
  tts    timestamp     not null
)
go
create unique index test_types_pk on test_types ( id )
go
grant all on test_types to test
go


-- test_keygen
drop table test_keygen
go
create table test_keygen (
  id   int          not null,
  attr varchar(200) not null
)
go
create unique index test_keygen_pk on test_keygen ( id )
go
grant all on test_keygen to test
go
 

-- test_keygen_ext
drop table test_keygen_ext
go
create table test_keygen_ext (
  id   int          not null,
  ext  varchar(200) not null
)
go
create unique index test_keygen_ext_pk on test_keygen_ext ( id )
go
grant all on test_keygen_ext to test
go


-- test_seqtable
drop table test_seqtable
go
create table test_seqtable (
  table_name varchar(200) not null,
  max_id     int          null
)
go
create unique index test_seqtable_pk on test_seqtable ( table_name )
go
grant all on test_seqtable to test
go




