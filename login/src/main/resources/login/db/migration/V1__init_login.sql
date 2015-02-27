create schema if not exists LOGIN;

create table if not exists LOGIN.users(
--create table if not exists users(
  userid bigint generated by default as identity(start with 0) primary key,
  username varchar_ignorecase(50) not null,
  enabled boolean not null);

create table if not exists LOGIN.user_credentials(
  userid  bigint not null,
  password varchar_ignorecase(60) not null,
  enabled boolean not null,
  expires timestamp,
  constraint fk_passwords_users foreign key(userid) references LOGIN.users(userid));
 
create unique index LOGIN.ix_username_password on LOGIN.user_credentials (userid, password);
  
create table if not exists LOGIN.authorities (
--create table if not exists authorities (
  username varchar_ignorecase(50) not null,
  authority varchar_ignorecase(50) not null,
  constraint fk_authorities_users foreign key(username) references LOGIN.users(username));

create unique index LOGIN.ix_auth_username on LOGIN.authorities (username, authority);
--create unique index ix_auth_username on authorities (username,authority);

create table if not exists LOGIN.groups (
--create table if not exists groups (
  id bigint generated by default as identity(start with 0) primary key,
  group_name varchar_ignorecase(50) not null);

create table if not exists LOGIN.group_authorities (
--create table if not exists group_authorities (
  group_id bigint not null,
  authority varchar(50) not null,
  constraint fk_group_authorities_group foreign key(group_id) references LOGIN.groups(id));

create table if not exists LOGIN.group_members (
--create table if not exists group_members (
  id bigint generated by default as identity(start with 0) primary key,
  username varchar(50) not null,
  group_id bigint not null,
  constraint fk_group_members_group foreign key(group_id) references LOGIN.groups(id));

create table if not exists LOGIN.persistent_logins (
--create table if not exists persistent_logins (
  username varchar(64) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null);
  
--create table users(
--  username varchar_ignorecase(50) not null primary key,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null);
--NOTE: password == '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq'
insert into LOGIN.users ( username, enabled ) values ( 'email', true );
insert into LOGIN.users ( username, enabled ) values ( 'testuser@vyllage.com', true );
insert into LOGIN.users ( username, enabled ) values ( 'deana@vyllage.com', true );
insert into LOGIN.users ( username, enabled ) values ( 'someone@vyllage.com', true );

--create table if not exists LOGIN.user_credentials(
--  userid  bigint not null,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null,
--  expires timestamp not null,
--  constraint fk_passwords_users foreign key(userid) references LOGIN.users(userid));
insert into LOGIN.user_credentials (userid, password, enabled, expires) values (0, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into LOGIN.user_credentials (userid, password, enabled, expires) values (1, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into LOGIN.user_credentials (userid, password, enabled, expires) values (2, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into LOGIN.user_credentials (userid, password, enabled, expires) values (3, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);

--create table authorities (
--  username varchar_ignorecase(50) not null,
--  authority varchar_ignorecase(50) not null,
--  constraint fk_authorities_users foreign key(username) references users(username));
--  create unique index ix_auth_username on authorities (username,authority);
insert into LOGIN.authorities ( username, authority ) values ( 'email', 'ADMIN' );
insert into LOGIN.authorities ( username, authority ) values ( 'email', 'USER' );
insert into LOGIN.authorities ( username, authority ) values ( 'testuser@vyllage.com', 'USER' );
insert into LOGIN.authorities ( username, authority ) values ( 'deana@vyllage.com', 'ADVISOR' );
insert into LOGIN.authorities ( username, authority ) values ( 'someone@vyllage.com', 'ADVISOR' );

--create table groups (
--  id bigint generated by default as identity(start with 0) primary key,
--  group_name varchar_ignorecase(50) not null);
insert into LOGIN.groups ( group_name ) values ( 'admins' );
insert into LOGIN.groups ( group_name ) values ( 'users' );
insert into LOGIN.groups ( group_name ) values ( 'advisors' );

--create table group_authorities (
--  group_id bigint not null,
--  authority varchar(50) not null,
--  constraint fk_group_authorities_group foreign key(group_id) references groups(id));
insert into LOGIN.group_authorities ( group_id, authority ) values ( 0, 'ADMIN' );
insert into LOGIN.group_authorities ( group_id, authority ) values ( 1, 'USER' );
insert into LOGIN.group_authorities ( group_id, authority ) values ( 2, 'ADVISOR' );

--create table group_members (
--  id bigint generated by default as identity(start with 0) primary key,
--  username varchar(50) not null,
--  group_id bigint not null,
--  constraint fk_group_members_group foreign key(group_id) references groups(id));
insert into LOGIN.group_members ( username, group_id ) values ( 'email', 0 );
insert into LOGIN.group_members ( username, group_id ) values ( 'testuser@vyllage.com', 1 );
insert into LOGIN.group_members ( username, group_id ) values ( 'deana@vyllage.com', 2 );
insert into LOGIN.group_members ( username, group_id ) values ( 'someone@vyllage.com', 0 );

