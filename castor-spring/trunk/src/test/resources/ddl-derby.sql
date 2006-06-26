connect 'jdbc:derby://127.0.0.1/spring;create=true;user=spring;password=spring';

drop table product;
create table product (
	id int not null,
	name varchar (20) not null,
	primary key (id)
);

insert into product (id, name) values (1, 'product1');
