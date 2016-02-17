create table if not exists DOCUMENTS.job_offers(
	job_offer_id bigserial primary key,
	user_id bigint not null,
	organization_id bigint not null,
	job_type varchar(50),
	job_experience varchar(50),
	salary decimal(20,2),
	location varchar(256),
	company varchar(50),
	role varchar(75),
  	description varchar(4096),
  	requires_relocation boolean,
  	remote boolean,
  	date_created timestamp not null,
  	last_modified timestamp not null);

create table if not exists DOCUMENTS.job_responsibility(
	job_responsibility_id bigserial primary key,
	job_offer_id bigint not null,
	description varchar(4096),
  	date_created timestamp not null,
  	last_modified timestamp not null);
