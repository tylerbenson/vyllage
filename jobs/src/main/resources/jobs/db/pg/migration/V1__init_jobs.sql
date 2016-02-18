create schema if not exists jobs;

create table if not exists jobs.job_offers(
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
  	requires_relocation boolean not null,
  	remote boolean not null,
  	site_wide boolean not null,
  	date_created timestamp not null,
  	last_modified timestamp not null);