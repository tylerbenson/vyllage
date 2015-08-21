alter table accounts.organizations add column external_id varchar_ignorecase(64);
update accounts.organizations set external_id = '2c2d9edb89c64a6ca77ed459866925b1' where organization_id = 1;