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
	value varchar(100),
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

create table if not exists accounts.organizations (
  organization_id bigserial primary key,
  organization_name varchar(50) not null);

create table if not exists accounts.user_organization_roles (
  user_id bigint not null,
  organization_id bigint not null,
  role varchar(50) not null,
  date_created timestamp not null,
  audit_user_id bigint not null,
  constraint fk_user_organization_roles_users foreign key(user_id) references accounts.users(user_id),
  constraint fk_user_organization_roles_organization foreign key(organization_id) references accounts.organizations(organization_id),
  constraint fk_user_organization_roles_role foreign key(role) references accounts.roles(role),
  constraint fk_user_organization_roles_users_audit foreign key(audit_user_id) references accounts.users(user_id));

CREATE TABLE if not exists accounts.lms_type (
  type_id bigserial ,
  lms_name character(20),
  date_created timestamp not null,
  CONSTRAINT lms_type_pkey PRIMARY KEY (type_id)
);

CREATE TABLE if not exists accounts.lms (
  lms_id bigserial ,
  lms_guid character (50) NOT NULL,
  lms_name character(50),
  lms_version character(20),
  lms_type_id bigint NOT NULL,
  lti_version character(20),
  oauth_version character(20),
  organization_id bigint NOT NULL,
  date_created timestamp not null,
  last_modified timestamp not null,
  CONSTRAINT lms_pkey PRIMARY KEY (lms_id),
  CONSTRAINT lms_lms_type_id_fkey FOREIGN KEY (lms_type_id) REFERENCES accounts.lms_type (type_id),
  CONSTRAINT lms_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES accounts.organizations (organization_id) );

CREATE TABLE if not exists accounts.lms_user_credentials (
  lms_user_id character(100) NOT NULL ,
  password character(50) NOT NULL,
  enabled boolean,
  expires timestamp,
  user_id bigint NOT NULL,
  lms_id bigint NOT NULL,
  CONSTRAINT lms_user_credentials_pkey PRIMARY KEY (lms_user_id),
  CONSTRAINT lms_user_credentials_lms_id_fkey FOREIGN KEY (lms_id) REFERENCES accounts.lms (lms_id),
  CONSTRAINT lms_user_credentials_user_id_fkey FOREIGN KEY (user_id)  REFERENCES accounts.users (user_id));