create schema if not exists documents;
  	
create table if not exists documents.documents(
	id bigserial primary key,
	userid bigint not null,
	tagline varchar(50),
  	visibility boolean not null,
  	datecreated timestamp not null,
  	lastmodified timestamp not null);
  	
create table if not exists documents.document_sections(
	id bigserial not null,
	sectionversion bigint not null,
	documentid bigint not null,
  	position bigint not null,
  	jsondocument varchar(4096) not null,
  	datecreated timestamp not null,
  	lastmodified timestamp not null,
  	primary key (id, sectionversion),
  	constraint fk_section_documents foreign key(documentid) references documents.documents(id) );
  	
create table if not exists documents.comments(
	comment_id bigserial primary key,
	user_id bigint not null,
	section_id bigint not null,
	section_version bigint not null,
	other_comment_id bigint,
	comment_text varchar(2048),
  	last_modified timestamp not null,
  	constraint fk_comments_document_sections foreign key(section_id, section_version) references documents.document_sections(id, sectionversion) ) ;
  	
create table if not exists documents.suggestions(
	suggestion_id bigserial primary key,
	user_id bigint not null,
	section_id bigint not null,
	section_version bigint not null,
	json_document varchar(4096) not null,
 	last_modified timestamp not null,
	constraint fk_suggestions_document_sections foreign key(section_id, section_version) references documents.document_sections(id, sectionversion) );


