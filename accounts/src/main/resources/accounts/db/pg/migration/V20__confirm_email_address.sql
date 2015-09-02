create table if not exists accounts.emails(
	emails_id bigserial primary key,
	user_id bigint not null,
	email varchar(50) not null,
	default_email boolean not null,
	confirmed boolean not null default false,
	constraint fk_emails_users foreign key(user_id) references accounts.users(user_id))
);