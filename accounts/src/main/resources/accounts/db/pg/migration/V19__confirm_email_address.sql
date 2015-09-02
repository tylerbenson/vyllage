create table if not exists accounts.emails(
	emails_id bigint generated by default as identity(start with 0) primary key,
	user_id bigint not null,
	email varchar(50) not null,
	primary boolean not null,
	confirmed boolean not null default false,
	constraint fk_emails_users foreign key(user_id) references accounts.users(user_id))
);