
insert into DOCUMENTS.documents(user_id, visibility, tagline, date_created, last_modified) values(0, true, 'My tagline.', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
--Mario
insert into DOCUMENTS.documents(user_id, visibility, tagline, date_created, last_modified) values(3, true, 'Awesome adventurous plumber.', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(123, 1, 0, 1, '{
	"type": "freeform",
	"title": "career goal",
	"sectionId": 123,
	"sectionPosition": 1,
	"state": "shown",
	"description": "This is my goal statement."
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(124, 1, 0, 2, '{
	"type": "organization",
	"title": "experience",
	"sectionId": 124,
	"sectionPosition": 2,
	"state": "shown",
	"organizationName": "DeVry Education Group",
	"organizationDescription": "An education group.",
	"role": "Manager, Local Accounts",
	"startDate": "Sep 2010",
	"endDate": "",
	"isCurrent": true,
	"location": "Portland, Oregon",
	"roleDescription": "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?",
	"highlights": "I was in charge of..."
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(125, 1, 0, 3, '{
	"type": "organization",
	"title": "education",
	"sectionId": 125,
	"sectionPosition": 3,
	"state": "hidden",
	"organizationName": "Keller Graduate School of Management",
	"organizationDescription": "Management School.",
	"role": "Masters of Project Management",
	"startDate": "Sep 2010",
	"endDate": "Sep 2012",
	"isCurrent": false,
	"location": "Portland, Oregon",
	"roleDescription": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
	"highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(126, 1, 0, 4, '{
	"type": "freeform",
	"title": "skills",
	"sectionId": 126,
	"sectionPosition": 4,
	"description": "rendis doloribus asperiores repellat."
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.comments(section_Id, section_Version, user_id, comment_Text, last_Modified) values(124, 1, 3, 'Its a me, Mario!', CURRENT_TIMESTAMP());
insert into DOCUMENTS.comments(section_Id, section_Version, user_id, comment_Text, last_Modified) values(126, 1, 2, 'Well played.', CURRENT_TIMESTAMP());

insert into DOCUMENTS.suggestions(section_Id, section_Version, user_id, json_Document, last_Modified) values (124, 1, 0, '{
	"type": "freeform",
	"title": "career goal",
	"sectionId": 123,
	"sectionPosition": 1,
	"state": "shown",
	"description": "This is my AWESOME goal statement."
}', CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_access(document_id, user_id, access, date_created, last_modified, expiration_date) 
values(0, 3, 'READ', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), null);