drop table JOBS.job_offers;

create table if not exists JOBS.job_opening(
	job_opening_id bigint generated by default as identity(start with 0) primary key,
	user_id bigint not null,
	organization_id bigint not null,
	job_type varchar_ignorecase(50),
	job_experience varchar_ignorecase(50),
	salary decimal(20,2),
	location varchar_ignorecase(256),
	company varchar_ignorecase(50),
	role varchar_ignorecase(75),
  	description varchar_ignorecase(4096),
  	requires_relocation boolean not null,
  	remote boolean not null,
  	site_wide boolean not null,
  	date_created timestamp not null,
  	last_modified timestamp not null);
  	
insert into JOBS.job_opening(job_opening_id, user_id, organization_id, job_type, job_experience, salary, location, 
company, role, description, 
requires_relocation, remote, site_wide, date_created, last_modified)
values(0, 0, 1,  'fulltime', 'fresh_graduate', 1000.00, 'Buenos Aires', 'Google', 'Corporate Operations Engineer, IT Support Technician', 
'Technical support for a technology company is a big task. As a Corporate Operations Engineer you are the go-to person for Googlers'' computer hardware and software needs, providing front line user support for all of Google''s internal tools and technologies. You troubleshoot, respond to inquiries and find solutions to technical challenges. Beyond the day-to-day, you improve the Googler user experience by contributing to longer term projects and documentation efforts. You are highly technical and are comfortable problem solving with multiple operating systems (like OS X, Linux, Windows) and a range of devices (including desktops/laptops, phone systems, video conferencing and various wireless devices). You occasionally partner with various teams including security, networking and infrastructure. You''re a fast learner and great communicator who can support the IT needs of global offices of all sizes and Googlers of varying technical backgrounds.
You improve the front-line user experience by providing on-demand user support for Google''s corporate users, resources, tools and applications while also contributing to longer-term projects.', 
false, false, false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());