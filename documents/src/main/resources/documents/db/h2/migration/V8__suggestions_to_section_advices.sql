alter table documents.suggestions rename to section_advices;
alter table documents.section_advices alter column suggestion_id rename to section_advice_id;
alter table documents.section_advices drop constraint fk_suggestions_document_sections;

alter table documents.section_advices add constraint fk_section_advices_document_sections 
foreign key(section_id, section_version) references documents.document_sections(id, sectionVersion) on delete cascade;