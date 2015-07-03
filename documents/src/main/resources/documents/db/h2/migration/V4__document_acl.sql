create table if not exists DOCUMENTS.document_access(
	document_id bigint not null,
	user_id bigint not null,
	access varchar_ignorecase(10) not null,
  	date_created timestamp not null,
  	last_modified timestamp not null,
  	PRIMARY KEY(document_id, user_id),
  	constraint fk_document_access_documents foreign key(document_Id) references DOCUMENTS.documents(document_id) on delete cascade);