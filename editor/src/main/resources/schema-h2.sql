create table if not exists accounts(
	id bigint generated by default as identity(start with 0) primary key,
  	username varchar_ignorecase(50) not null );
  	
create table if not exists documents(
	id bigint generated by default as identity(start with 0) primary key,
	accountId bigint not null,
  	visibility boolean not null,
  	dateCreated datetime not null,
  	lastModified timestamp not null,
  	constraint fk_documents_accounts foreign key(accountId) references accounts(id));
  	
create table if not exists document_sections(
	id bigint not null AUTO_INCREMENT,
	sectionVersion bigint not null,
	documentId bigint not null,
  	sortOrder varchar(3) not null,
  	jsonDocument varchar(1024) not null,
  	dateCreated datetime not null,
  	lastModified timestamp not null,
  	PRIMARY KEY (id, sectionVersion),
  	constraint fk_section_documents foreign key(documentId) references documents(id));
  	
create table if not exists comments(
	id bigint generated by default as identity(start with 0) primary key,
	sectionId bigint not null,
	username varchar_ignorecase(50) not null,
	commentId bigint,
  	lastModified timestamp not null,
  	constraint fk_comments_document_sections foreign key(sectionId) references document_sections(id));
  	
create table if not exists suggestions(
	id bigint generated by default as identity(start with 0) primary key,
	sectionId bigint not null,
	sectionVersion bigint not null,
	username varchar_ignorecase(50) not null,
  	lastModified timestamp not null,
  	constraint fk_suggestions_document_sections foreign key(sectionId, sectionVersion) references document_sections(id, sectionVersion));