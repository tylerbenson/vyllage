create table if not exists documents.document_access(
	document_id bigint not null,
	user_id bigint not null,
	access varchar(10) not null,
  	date_created timestamp not null,
  	last_modified timestamp not null,
  	primary key(document_id, user_id),
  	constraint fk_document_access_documents foreign key(document_Id) references documents.documents(document_id));