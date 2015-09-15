insert into accounts.roles ( role ) values ( 'ADVISOR' );
insert into accounts.roles ( role ) values ( 'ADMIN' );
insert into accounts.roles ( role ) values ( 'ALUMNI' );
insert into accounts.roles ( role ) values ( 'GUEST' );
insert into accounts.roles ( role ) values ( 'STAFF' );
insert into accounts.roles ( role ) values ( 'STUDENT' );

insert into accounts.users (user_id, user_name, first_name, middle_name, last_name, enabled, date_created, last_modified) values (0, 'nathan@vyllage.com', 'Nathan', 'M', 'Benson', true, current_date, current_date);
insert into accounts.users (user_id, user_name, first_name, middle_name, last_name, enabled, date_created, last_modified) values (1, 'tyler@vyllage.com', 'Tyler', 'D', 'Benson', true, current_date, current_date);

insert into accounts.user_credentials (user_id, password, enabled, expires) values (0, '$2a$10$AD3tneImR8CkAiESv.zDruYMGjm5rNnmQEW22VmiRq8wgjlt1WfNm', true, null);
insert into accounts.user_credentials (user_id, password, enabled, expires) values (1, '$2a$10$AD3tneImR8CkAiESv.zDruYMGjm5rNnmQEW22VmiRq8wgjlt1WfNm', true, null);

insert into accounts.organizations ( organization_id, organization_name ) values ( 0, 'Vyllage' );
insert into accounts.organizations ( organization_id, organization_name ) values ( 1, 'Test' );

insert into accounts.user_organization_roles ( organization_id, user_id, role, date_created, audit_user_id ) values ( 0, 0, 'ADMIN', current_date, 0 );
insert into accounts.user_organization_roles ( organization_id, user_id, role, date_created, audit_user_id ) values ( 0, 1, 'ADMIN', current_date, 0 );