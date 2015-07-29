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
  
insert into ACCOUNTS.roles ( role ) values ( 'LMS_ADMIN' );
insert into ACCOUNTS.roles ( role ) values ( 'INSTRUCTOR' );
insert into ACCOUNTS.roles ( role ) values ( 'TEACHING_ASSISTANT' );

insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 0, 'BLACKBOARD', current_date);
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 1, 'SAKAI' , current_date);
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 2, 'DESIRE2LEARN', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 3, 'CANVAS', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 4, 'MOODLE', current_date );
insert into ACCOUNTS.lms_type ( type_id, lms_name , date_created ) values ( 5, 'CUSTOM' , current_date );
