create schema if not exists CONNECTIONS;

create table if not exists CONNECTIONS.contacts(
	id bigserial primary key,
  	username varchar(50) not null );
  	

