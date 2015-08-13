create table if not exists accounts.lms_key(
  key_id bigint primary key,
  consumer_key varchar(256) not null unique,
  secret varchar(256) not null,
  creator_user_id bigint not null,
  modified_by_user_id bigint not null,
  date_created timestamp not null,
  last_modified timestamp not null,
  constraint fk_lms_key_organization_organization foreign key(key_id) references accounts.organizations(organization_id),
  constraint fk_lms_key_creator_users foreign key(creator_user_id) references accounts.users(user_id), 
  constraint fk_lms_key_modified_users foreign key(modified_by_user_id) references accounts.users(user_id));