alter table documents.document_sections drop constraint fk_section_documents;
alter table documents.document_sections add constraint fk_section_documents foreign key(documentId) references documents.documents(document_id) on delete cascade;

alter table documents.comments drop constraint fk_comments_document_sections;
alter table documents.comments add constraint fk_comments_document_sections foreign key(section_id, section_version) references documents.document_sections(id, sectionversion) on delete cascade;

alter table documents.suggestions drop constraint fk_suggestions_document_sections;
alter table documents.suggestions add constraint fk_suggestions_document_sections foreign key(section_id, section_version) references documents.document_sections(id, sectionversion) on delete cascade;