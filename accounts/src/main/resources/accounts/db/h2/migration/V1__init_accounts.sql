create schema if not exists ACCOUNTS;

create table if not exists ACCOUNTS.users(
  user_id bigint generated by default as identity(start with 0) primary key,
  user_name varchar_ignorecase(50) not null,
  first_name varchar_ignorecase(50),
  middle_name varchar_ignorecase(50),
  last_name varchar_ignorecase(50),
  enabled boolean not null,
  date_created timestamp not null,
  last_modified timestamp not null);

create table if not exists ACCOUNTS.personal_information(
	user_id bigint not null primary key,
	graduation_date timestamp,
	email_updates varchar_ignorecase(15),
	phone_number varchar_ignorecase(20),
	address varchar_ignorecase(30),
	constraint fk_personal_users foreign key(user_id) references ACCOUNTS.users(user_id));

create table if not exists ACCOUNTS.account_settings(
	account_setting_id bigint generated by default as identity(start with 0) primary key,
	user_id bigint not null,
	name varchar(15),
	"value" varchar(30),
	privacy varchar(15),
	constraint fk_account_settings_users foreign key(user_id) references ACCOUNTS.users(user_id));
  
create table if not exists ACCOUNTS.user_credentials(
  user_id  bigint not null,
  password varchar_ignorecase(60) not null,
  enabled boolean not null,
  expires timestamp,
  constraint fk_passwords_users foreign key(user_id) references ACCOUNTS.users(user_id));

create unique index ACCOUNTS.ix_username_password on ACCOUNTS.user_credentials (user_id, password);
  
create table if not exists ACCOUNTS.roles (
  user_name varchar_ignorecase(50) not null,
  role varchar_ignorecase(50) not null,
  constraint fk_roles_users foreign key(user_name) references ACCOUNTS.users(user_name));

create unique index ACCOUNTS.ix_auth_username on ACCOUNTS.roles (user_name, role);

create table if not exists ACCOUNTS.organizations (
  organization_id bigint generated by default as identity(start with 0) primary key,
  organization_name varchar_ignorecase(50) not null);

create table if not exists ACCOUNTS.organization_roles (
  organization_id bigint not null,
  role varchar(50) not null,
  constraint fk_organization_roles_organization foreign key(organization_id) references ACCOUNTS.organizations(organization_id));

create table if not exists ACCOUNTS.organization_members (
  id bigint generated by default as identity(start with 0) primary key,
  user_id bigint not null,
  organization_id bigint not null,
  constraint fk_organization_members_organization foreign key(organization_id) references ACCOUNTS.organizations(organization_id),
  constraint fk_organization_members_users foreign key(user_id) references ACCOUNTS.users(user_id));

create table if not exists ACCOUNTS.persistent_logins (
  user_name varchar(64) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null);
  
