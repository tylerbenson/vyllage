create table if not exists ACCOUNTS.shared_document(
	short_url varchar_ignorecase(40) not null primary key,
	link_type varchar_ignorecase(40),
	document_id bigint,
	document_type varchar_ignorecase(30),
	user_id bigint not null,
	generated_password varchar_ignorecase(128),
	expiration_date timestamp, 	
  	visits bigint,
  	date_created timestamp not null);
 
