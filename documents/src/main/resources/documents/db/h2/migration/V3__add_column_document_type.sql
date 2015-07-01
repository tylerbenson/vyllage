alter table DOCUMENTS.documents add document_type varchar_ignorecase(50) not null DEFAULT 'RESUME';
--set all to resume, it's the only type we have right now
update DOCUMENTS.documents set document_type = 'RESUME';