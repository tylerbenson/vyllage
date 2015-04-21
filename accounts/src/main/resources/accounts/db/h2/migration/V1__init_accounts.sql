create schema if not exists ACCOUNTS;

create table if not exists ACCOUNTS.users(
  user_id bigint generated by default as identity(start with 0) primary key,
  user_name varchar_ignorecase(50) not null unique,
  first_name varchar_ignorecase(50),
  middle_name varchar_ignorecase(50),
  last_name varchar_ignorecase(50),
  enabled boolean not null,
  date_created timestamp not null,
  last_modified timestamp not null);

create table if not exists ACCOUNTS.account_setting(
	account_setting_id bigint generated by default as identity(start with 0) primary key,
	user_id bigint not null,
	name varchar(15),
	value varchar(30),
	privacy varchar(15),
	constraint fk_account_setting_users foreign key(user_id) references ACCOUNTS.users(user_id));

create table if not exists ACCOUNTS.user_credentials(
  user_id  bigint not null,
  password varchar_ignorecase(60) not null,
  enabled boolean not null,
  expires timestamp,
  constraint fk_passwords_users foreign key(user_id) references ACCOUNTS.users(user_id));

create unique index ACCOUNTS.ix_username_password on ACCOUNTS.user_credentials (user_id, password);

create table if not exists ACCOUNTS.roles(
	role varchar_ignorecase(50) primary key);

create table if not exists ACCOUNTS.organizations (
  organization_id bigint generated by default as identity(start with 0) primary key,
  organization_name varchar_ignorecase(50) not null);

create table if not exists ACCOUNTS.user_organization_roles (
  user_id bigint not null,
  organization_id bigint not null,
  role varchar(50) not null,
  date_created timestamp not null,
  audit_user_id bigint not null,
  constraint fk_organization_roles_users foreign key(user_id) references ACCOUNTS.users(user_id),
  constraint fk_organization_roles_organization foreign key(organization_id) references ACCOUNTS.organizations(organization_id),
  constraint fk_organization_roles_role foreign key(role) references ACCOUNTS.roles(role),
  constraint fk_organization_roles_users_audit foreign key(audit_user_id) references ACCOUNTS.users(user_id));
