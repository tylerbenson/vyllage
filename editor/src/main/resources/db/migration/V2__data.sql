insert into EDITOR.accounts(username) values('username');

insert into EDITOR.documents(accountId, visibility, dateCreated, lastModified) values(0, true, CURRENT_DATE(), CURRENT_DATE());

insert into EDITOR.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(123, 1, 0, 1, '{
	"type": "freeform",
	"title": "career goal",
	"sectionId": 123,
	"sectionPosition": 1,
	"state": "shown",
	"description": "this is my goal statement."
}',CURRENT_DATE(), CURRENT_DATE());

insert into EDITOR.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(124, 1, 0, 2, '{
	"type": "experience",
	"title": "job experience",
	"sectionId": 124,
	"sectionPosition": 2,
	"state": "shown",
	"organizationName": "DeVry Education Group",
	"organizationDescription": "Blah Blah Blah.",
	"role": "Manager, Local Accounts",
	"startDate": "September 2010",
	"endDate": "",
	"isCurrent": true,
	"location": "Portland, Oregon",
	"roleDescription": "Blah Blah Blah",
	"highlights": "I was in charge of..."
}',CURRENT_DATE(), CURRENT_DATE());

insert into EDITOR.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(125, 1, 0, 3, '{
	"type": "experience",
	"title": "education",
	"sectionId": 125,
	"sectionPosition": 3,
	"state": "hidden",
	"organizationName": "Keller Graduate School of Management",
	"organizationDescription": "Blah Blah Blah.",
	"role": "Masters of Project Management",
	"startDate": "September 2010",
	"endDate": "September 2012",
	"isCurrent": false,
	"location": "Portland, Oregon",
	"roleDescription": "",
	"highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
}',CURRENT_DATE(), CURRENT_DATE());

insert into EDITOR.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(126, 1, 0, 4, '{
	"type": "freeform",
	"title": "skills",
	"sectionId": 126,
	"sectionPosition": 4,
	"description": "basket weaving, spear fishing, dominion"
}',CURRENT_DATE(), CURRENT_DATE());

insert into EDITOR.comments(sectionId, username, commentText, lastModified) values(124, 'mario', 'Its a me, Mario!', CURRENT_DATE());
insert into EDITOR.comments(sectionId, username, commentText, lastModified) values(126, 'pretendergod', 'Well played.', CURRENT_DATE());

insert into EDITOR.suggestions(sectionId, sectionVersion, username, jsonDocument, lastModified) values (123, 1, 'link', '{
	"type": "freeform",
	"title": "career goal",
	"sectionId": 123,
	"sectionPosition": 1,
	"state": "shown",
	"description": "this is my AWESOME goal statement."
}', CURRENT_DATE());



