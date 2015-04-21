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
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'organization', 'stanford', 'public');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'role', 'student', 'public');
insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (0, 'email', 'email', 'private');


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

insert into ACCOUNTS.roles ( role ) values ( 'ADVISOR' );
insert into ACCOUNTS.roles ( role ) values ( 'ADMIN' );
insert into ACCOUNTS.roles ( role ) values ( 'STAFF' );
insert into ACCOUNTS.roles ( role ) values ( 'STUDENT' );
insert into ACCOUNTS.roles ( role ) values ( 'ALUMNI' );
insert into ACCOUNTS.roles ( role ) values ( 'GUEST' );

--create table groups (
--  id bigint generated by default as identity(start with 0) primary key,
--  group_name varchar_ignorecase(50) not null);
insert into accounts.organizations ( organization_id, organization_name ) values ( 0, 'Vyllage' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 1, 'Guest' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 2, 'University 1' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 3, 'University 2' );

--create table group_authorities (
--  group_id bigint not null,
--  user_id bigint not null,
--  authority varchar(50) not null,
--  constraint fk_group_authorities_group foreign key(group_id) references groups(id));
insert into ACCOUNTS.user_organization_roles ( organization_id, user_id, role ) values ( 0, 0, 'ADMIN' );
insert into ACCOUNTS.user_organization_roles ( organization_id, user_id, role ) values ( 0, 2, 'ADVISOR' );
insert into ACCOUNTS.user_organization_roles ( organization_id, user_id, role ) values ( 2, 1, 'STUDENT' );
insert into ACCOUNTS.user_organization_roles ( organization_id, user_id, role ) values ( 1, 3, 'ADMIN' );
insert into ACCOUNTS.user_organization_roles ( organization_id, user_id, role ) values ( 2, 2, 'STAFF' );
