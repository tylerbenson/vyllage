alter table accounts.lti_credentials add column external_organization_id varchar_ignorecase(64) not null;

alter table accounts.lti_credentials add constraint uq_external_organization_id unique (external_organization_id);

create unique index accounts.ix_organization_external_organization on accounts.lti_credentials (key_id, external_organization_id);

insert into accounts.lti_credentials (key_id, consumer_key, secret, creator_user_id, modified_by_user_id, date_created, last_modified, external_organization_id) 
values (1, 'University12323213', 'vl5i0tQwvUoEsPJi9rBS', 1, 1, CURRENT_DATE(), CURRENT_DATE(), 'FFgdPrsCVn8zKFpDUkFF2TaXzo6zIVULifyHjz8J:canvas-lms');	

insert into accounts.lti_credentials (key_id, consumer_key, secret, creator_user_id, modified_by_user_id, date_created, last_modified, external_organization_id) 
values (2, 'University2abc2009', 'sad13k3lkl3eRtk91sXV', 1, 1, CURRENT_DATE(), CURRENT_DATE(), '2c2d9edb89c64a6ca77ed459866925b1');	

