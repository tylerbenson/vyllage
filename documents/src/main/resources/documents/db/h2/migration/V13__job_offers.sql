create table if not exists DOCUMENTS.job_offers(
	job_offer_id bigint generated by default as identity(start with 0) primary key,
	user_id bigint not null,
	job_type varchar_ignorecase(50),
	job_experience varchar_ignorecase(50),
	salary decimal(20,2),
	location varchar_ignorecase(256),
  	requires_relocation boolean,
  	remote boolean,
  	date_created timestamp not null,
  	last_modified timestamp not null);
  	
insert into DOCUMENTS.job_offers(job_offer_id, user_id, job_type, job_experience, salary, location, requires_relocation, remote, date_created, last_modified)
values(0, 0, 'fulltime', 'fresh_graduate', 1000.00, 'Buenos Aires', false, false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());