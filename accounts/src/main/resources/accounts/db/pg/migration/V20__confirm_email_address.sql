create table if not exists accounts.emails(
	email_id bigserial primary key,
	user_id bigint not null,
	email varchar(50) not null unique,
	default_email boolean not null,
	confirmed boolean not null default false,
	date_created timestamp not null,
  	last_modified timestamp not null,
	constraint fk_emails_users foreign key(user_id) references accounts.users(user_id)
);

insert into accounts.emails (user_id, email, default_email, confirmed, date_created, last_modified)  
select user_id, user_name, true, true, current_date, current_date from accounts.users;