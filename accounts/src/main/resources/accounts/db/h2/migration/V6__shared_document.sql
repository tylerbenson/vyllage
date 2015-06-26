create table if not exists ACCOUNTS.shared_document(
	link_key varchar_ignorecase(40) not null primary key,
	link_type varchar_ignorecase(40),
	document_id bigint,
	document_type varchar_ignorecase(30),
	user_id bigint not null,
	expiration_date timestamp, 	
  	visits bigint,
  	date_created timestamp not null);
 
