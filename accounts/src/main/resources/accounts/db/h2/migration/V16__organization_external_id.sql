alter table accounts.organizations add column external_id varchar_ignorecase(64);
update accounts.organizations set external_id = 'FFgdPrsCVn8zKFpDUkFF2TaXzo6zIVULifyHjz8J:canvas-lms' where organization_id = 1;
update accounts.organizations set external_id = '2c2d9edb89c64a6ca77ed459866925b1' where organization_id = 2;
--update accounts.organizations set external_id = '2c2d9edb89c64a6ca77ed459866925b1' where organization_id = 1;
insert into accounts.lti_credentials (key_id, consumer_key, secret, creator_user_id, modified_by_user_id, date_created, last_modified) 
values (1, 'University12323213', 'vl5i0tQwvUoEsPJi9rBS', 1, 1, CURRENT_DATE(), CURRENT_DATE());