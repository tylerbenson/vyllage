create table if not exists ACCOUNTS.lms_type (
  type_id bigserial ,
  lms_name character(20),
  date_created timestamp not null,
  constraint lms_type_pkey primary key (type_id)
);

create table if not exists ACCOUNTS.lms (
  lms_id bigserial ,
  lms_guid character (80) NOT NULL,
  lms_name character(50),
  lms_version character(20),
  lms_type_id bigint NOT NULL,
  lti_version character(20),
  oauth_version character(20),
  organization_id bigint NOT NULL,
  date_created timestamp not null,
  last_modified timestamp not null,
  constraint lms_pkey primary key (lms_id),
  constraint lms_lms_type_id_fkey foreign key (lms_type_id) references accounts.lms_type (type_id),
  constraint lms_organization_id_fkey foreign key (organization_id) references accounts.organizations (organization_id) );

create table if not exists ACCOUNTS.lms_user_credentials (
  lms_user_id character(100) not null ,
  password character(50) not null,
  enabled boolean,
  expires timestamp,
  user_id bigint not null,
  lms_id bigint not null,
  constraint lms_user_credentials_pkey primary key (lms_user_id),
  constraint lms_user_credentials_lms_id_fkey foreign key (lms_id) references accounts.lms (lms_id),
  constraint lms_user_credentials_user_id_fkey foreign key (user_id)  references accounts.users (user_id));
  
insert into ACCOUNTS.roles ( role ) values ( 'LMS_ADMIN' );
insert into ACCOUNTS.roles ( role ) values ( 'INSTRUCTOR' );
insert into ACCOUNTS.roles ( role ) values ( 'TEACHING_ASSISTANT' );

insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 0, 'BLACKBOARD', current_date);
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 1, 'SAKAI' , current_date);
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 2, 'DESIRE2LEARN', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 3, 'CANVAS', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 4, 'MOODLE', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 5, 'CUSTOM' , current_date );
