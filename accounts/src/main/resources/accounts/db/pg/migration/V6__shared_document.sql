create table if not exists ACCOUNTS.shared_document(
	link_key varchar(40) primary key,
	link_type varchar(40),
	document_id bigint,
	document_type varchar(30),
	user_id bigint not null,
	generated_password varchar(128),
	expiration_date timestamp, 	
  	visits bigint,
  	date_created timestamp not null);
 
