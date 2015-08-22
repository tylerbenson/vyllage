alter table accounts.lti_credentials add column external_organization_id varchar(64) not null;

alter table accounts.lti_credentials add constraint uq_external_organization_id unique (external_organization_id);

create unique index accounts.ix_organization_external_organization on accounts.lti_credentials (key_id, external_organization_id);