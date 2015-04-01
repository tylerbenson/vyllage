--create table users(
--  user_name varchar_ignorecase(50) not null primary key,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null);
--NOTE: password == '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq'
insert into ACCOUNTS.users ( user_name, first_Name, middle_name, last_name, enabled, date_created, last_modified) values ( 'email', 'Luke', 'V', 'Skywalker', true, CURRENT_DATE(), CURRENT_DATE());
insert into ACCOUNTS.users ( user_name, enabled, date_created, last_modified ) values ( 'testuser@vyllage.com', true, CURRENT_DATE(), CURRENT_DATE());
insert into ACCOUNTS.users ( user_name, first_Name, last_name, enabled, date_created, last_modified ) values ( 'deana@vyllage.com', 'Deana', 'Troi', true, CURRENT_DATE(), CURRENT_DATE() );
insert into ACCOUNTS.users ( user_name, first_Name, last_name, enabled, date_created, last_modified ) values ( 'mario@toadstool.com', 'Mario', 'Mario', true, CURRENT_DATE(), CURRENT_DATE() );

--create table if not exists ACCOUNTS.account_setting(
--	account_setting_id bigint generated by default as identity(start with 0) primary key,
--	user_id bigint not null,
--	name varchar(15),
--	value varchar(30),
--	privacy varchar(15),
--	constraint fk_account_setting_users foreign key(user_id) references ACCOUNTS.users(user_id));
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'emailUpdates', 'weekly', 'public');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'firstName', 'Luke', 'public');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'middleName', 'V', 'public');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'lastName', 'Skywalker', 'private');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'address', 'Avenida Siempreviva 123', 'public');


--create table if not exists ACCOUNTS.user_credentials(
--  user_id  bigint not null,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null,
--  expires timestamp not null,
--  constraint fk_passwords_users foreign key(user_id) references ACCOUNTS.users(user_id));
insert into ACCOUNTS.user_credentials (user_id, password, enabled, expires) values (0, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into ACCOUNTS.user_credentials (user_id, password, enabled, expires) values (1, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into ACCOUNTS.user_credentials (user_id, password, enabled, expires) values (2, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);
insert into ACCOUNTS.user_credentials (user_id, password, enabled, expires) values (3, '$2a$10$6REiiQD8i4FTE4D/X3.chOnlgxghwWMGcngksAjyQun0njGDQznxq', true, null);

--create table authorities (
--  user_name varchar_ignorecase(50) not null,
--  authority varchar_ignorecase(50) not null,
--  constraint fk_authorities_users foreign key(user_name) references users(user_name));
--  create unique index ix_auth_user_name on authorities (user_name,authority);
insert into ACCOUNTS.roles ( user_name, role ) values ( 'email', 'ADMIN' );
insert into ACCOUNTS.roles ( user_name, role ) values ( 'email', 'USER' );
insert into ACCOUNTS.roles ( user_name, role ) values ( 'testuser@vyllage.com', 'USER' );
insert into ACCOUNTS.roles ( user_name, role ) values ( 'deana@vyllage.com', 'ADVISOR' );
insert into ACCOUNTS.roles ( user_name, role ) values ( 'mario@toadstool.com', 'ADVISOR' );

--create table groups (
--  id bigint generated by default as identity(start with 0) primary key,
--  group_name varchar_ignorecase(50) not null);
insert into ACCOUNTS.organizations ( organization_name ) values ( 'admins' );
insert into ACCOUNTS.organizations ( organization_name ) values ( 'users' );
insert into ACCOUNTS.organizations ( organization_name ) values ( 'advisors' );

--create table group_authorities (
--  group_id bigint not null,
--  authority varchar(50) not null,
--  constraint fk_group_authorities_group foreign key(group_id) references groups(id));
insert into ACCOUNTS.organization_roles ( organization_id, role ) values ( 0, 'ADMIN' );
insert into ACCOUNTS.organization_roles ( organization_id, role ) values ( 1, 'USER' );
insert into ACCOUNTS.organization_roles ( organization_id, role ) values ( 2, 'ADVISOR' );

--create table group_members (
--  id bigint generated by default as identity(start with 0) primary key,
--  user_name varchar(50) not null,
--  group_id bigint not null,
--  constraint fk_group_members_group foreign key(group_id) references groups(id));
insert into ACCOUNTS.organization_members ( user_id, organization_id ) values ( 0, 0 );
insert into ACCOUNTS.organization_members ( user_id, organization_id ) values ( 1, 1 );
insert into ACCOUNTS.organization_members ( user_id, organization_id ) values ( 2, 0 );
insert into ACCOUNTS.organization_members ( user_id, organization_id ) values ( 2, 2 );
insert into ACCOUNTS.organization_members ( user_id, organization_id ) values ( 3, 0 );

