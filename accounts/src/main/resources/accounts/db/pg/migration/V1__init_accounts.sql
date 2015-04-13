create schema if not exists accounts;

create table if not exists accounts.users(
  user_id bigserial primary key,
  user_name varchar(50) not null unique,
  first_name varchar(50),
  middle_name varchar(50),
  last_name varchar(50),
  enabled boolean not null,
  date_created timestamp not null,
  last_modified timestamp not null);
  
create table if not exists ACCOUNTS.account_setting(
	account_setting_id bigserial primary key,
	user_id bigint not null,
	name varchar(15),
	value varchar(30),
	privacy varchar(15),
	constraint fk_account_setting_users foreign key(user_id) references ACCOUNTS.users(user_id)); 
	
create table if not exists accounts.user_credentials(
  user_id  bigint not null,
  password varchar(60) not null,
  enabled boolean not null,
  expires timestamp,
  constraint fk_passwords_users foreign key(user_id) references accounts.users(user_id));

create unique index ix_username_password on accounts.user_credentials (user_id, password);
  
create table if not exists accounts.roles(
	role varchar(50) primary key);

create table if not exists accounts.user_roles (
  user_name varchar(50) not null,
  role varchar(50) not null,
  constraint fk_roles_users foreign key(user_name) references accounts.users(user_name),
  constraint fk_user_roles_roles foreign key(role) references accounts.roles(role));

create unique index ix_auth_username on accounts.user_roles (user_name, role);

create table if not exists accounts.organizations (
  organization_id bigserial primary key,
  organization_name varchar(50) not null);

create table if not exists accounts.organization_roles (
  organization_id bigint not null,
  role varchar(50) not null,
  constraint fk_organization_roles_organization foreign key(organization_id) references accounts.organizations(organization_id));

create table if not exists accounts.organization_members (
  id bigserial primary key,
  user_id bigint not null,
  organization_id bigint not null,
  constraint fk_organization_members_organization foreign key(organization_id) references accounts.organizations(organization_id),
  constraint fk_organization_members_users foreign key(user_id) references accounts.users(user_id));

create table if not exists accounts.persistent_logins (
  user_name varchar(64) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null);
  