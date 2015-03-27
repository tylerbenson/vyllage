--create table users(
--  user_name varchar_ignorecase(50) not null primary key,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null);
--note: password == '$2a$10$6reiiqd8i4fte4d/x3.chonlgxghwwmgcngksajyqun0njgdqznxq'
insert into accounts.users (user_id, user_name, first_name, middle_name, last_name, enabled, date_created, last_modified) values (0, 'email', 'luke', 'v', 'skywalker', true, current_date, current_date);
insert into accounts.users (user_id, user_name, enabled, date_created, last_modified ) values (1, 'testuser@vyllage.com', true, current_date, current_date);
insert into accounts.users (user_id, user_name, first_name, last_name, enabled, date_created, last_modified ) values (2, 'deana@vyllage.com', 'deana', 'troi', true, current_date, current_date );
insert into accounts.users (user_id, user_name, first_name, last_name, enabled, date_created, last_modified ) values (3, 'mario@toadstool.com', 'mario', 'mario', true, current_date, current_date );

--create table if not exists ACCOUNTS.account_setting(
--	account_setting_id bigint generated by default as identity(start with 0) primary key,
--	user_id bigint not null,
--	name varchar(15),
--	value varchar(30),
--	privacy varchar(15),
--	constraint fk_account_setting_users foreign key(user_id) references ACCOUNTS.users(user_id));
insert into ACCOUNTS.account_setting(user_id, name, "value", privacy) values (0, 'emailUpdates', 'weekly', 'public');
insert into ACCOUNTS.account_setting(user_id, name, "value", privacy) values (0, 'firstName', null, 'public');
insert into ACCOUNTS.account_setting(user_id, name, "value", privacy) values (0, 'middleName', null, 'public');
insert into ACCOUNTS.account_setting(user_id, name, "value", privacy) values (0, 'lastName', null, 'private');
insert into ACCOUNTS.account_setting(user_id, name, "value", privacy) values (0, 'address', 'Avenida Siempreviva 123', 'public');

--create table if not exists accounts.user_credentials(
--  user_id  bigint not null,
--  password varchar_ignorecase(60) not null,
--  enabled boolean not null,
--  expires timestamp not null,
--  constraint fk_passwords_users foreign key(user_id) references accounts.users(user_id));
insert into accounts.user_credentials (user_id, password, enabled, expires) values (0, '$2a$10$zl4EVU2EuFrqf7vnwPYo4.d4IkgZBgEZQyqm1XpqyOPLW35AHzOWW', true, null);
insert into accounts.user_credentials (user_id, password, enabled, expires) values (1, '$2a$10$zl4EVU2EuFrqf7vnwPYo4.d4IkgZBgEZQyqm1XpqyOPLW35AHzOWW', true, null);
insert into accounts.user_credentials (user_id, password, enabled, expires) values (2, '$2a$10$zl4EVU2EuFrqf7vnwPYo4.d4IkgZBgEZQyqm1XpqyOPLW35AHzOWW', true, null);
insert into accounts.user_credentials (user_id, password, enabled, expires) values (3, '$2a$10$zl4EVU2EuFrqf7vnwPYo4.d4IkgZBgEZQyqm1XpqyOPLW35AHzOWW', true, null);

--create table authorities (
--  user_name varchar_ignorecase(50) not null,
--  authority varchar_ignorecase(50) not null,
--  constraint fk_authorities_users foreign key(user_name) references users(user_name));
--  create unique index ix_auth_user_name on authorities (user_name,authority);
insert into accounts.roles ( user_name, role ) values ( 'email', 'admin' );
insert into accounts.roles ( user_name, role ) values ( 'email', 'user' );
insert into accounts.roles ( user_name, role ) values ( 'testuser@vyllage.com', 'USER' );
insert into accounts.roles ( user_name, role ) values ( 'deana@vyllage.com', 'ADVISOR' );
insert into accounts.roles ( user_name, role ) values ( 'mario@toadstool.com', 'ADVISOR' );

--create table groups (
--  id bigint generated by default as identity(start with 0) primary key,
--  group_name varchar_ignorecase(50) not null);
insert into accounts.organizations ( organization_id, organization_name ) values ( 0, 'ADMINS' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 1, 'USERS' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 2, 'ADVISORS' );

--create table group_authorities (
--  group_id bigint not null,
--  authority varchar(50) not null,
--  constraint fk_group_authorities_group foreign key(group_id) references groups(id));
insert into accounts.organization_roles ( organization_id, role ) values ( 0, 'ADMIN' );
insert into accounts.organization_roles ( organization_id, role ) values ( 1, 'USER' );
insert into accounts.organization_roles ( organization_id, role ) values ( 2, 'ADVISOR' );

--create table group_members (
--  id bigint generated by default as identity(start with 0) primary key,
--  user_name varchar(50) not null,
--  group_id bigint not null,
--  constraint fk_group_members_group foreign key(group_id) references groups(id));
insert into accounts.organization_members ( user_id, organization_id ) values ( 0, 0 );
insert into accounts.organization_members ( user_id, organization_id ) values ( 1, 1 );
insert into accounts.organization_members ( user_id, organization_id ) values ( 2, 0 );
insert into accounts.organization_members ( user_id, organization_id ) values ( 2, 2 );
insert into accounts.organization_members ( user_id, organization_id ) values ( 3, 0 );

