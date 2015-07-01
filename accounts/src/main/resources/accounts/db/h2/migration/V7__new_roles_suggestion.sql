alter table ACCOUNTS.user_organization_roles drop constraint fk_organization_roles_role; 
update ACCOUNTS.roles set role = 'ACADEMIC_ADVISOR' where role = 'ADVISOR';
update ACCOUNTS.user_organization_roles set role = 'ACADEMIC_ADVISOR' where role = 'ADVISOR';
alter table ACCOUNTS.user_organization_roles add constraint fk_organization_roles_role foreign key(role) references ACCOUNTS.roles(role);

insert into ACCOUNTS.roles ( role ) values ( 'ADMISSIONS_ADVISOR' );
insert into ACCOUNTS.roles ( role ) values ( 'CAREER_ADVISOR' );
insert into ACCOUNTS.roles ( role ) values ( 'TRANSFER_ADVISOR' );