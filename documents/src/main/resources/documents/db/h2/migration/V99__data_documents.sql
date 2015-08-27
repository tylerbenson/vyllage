
insert into DOCUMENTS.documents(user_id, visibility, tagline, date_created, last_modified) values(0, true, 'My tagline.', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
--Mario
insert into DOCUMENTS.documents(user_id, visibility, tagline, date_created, last_modified) values(3, true, 'Awesome adventurous plumber.', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_access(document_id, user_id, access, date_created, last_modified, expiration_date) 
values(0, 3, 'READ', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), null);


-- new sections

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(129, 1, 0, 3, '{
	"type": "SummarySection",
	"title": "summary",
	"sectionId": 129,
	"sectionPosition": 3,
	"state": "shown",
	"description": "Rebellion fighter."
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(126, 1, 0, 4, '{
	"type": "EducationSection",
	"title": "education",
	"sectionId": 126,
	"sectionPosition": 5,
	"state": "shown",
	"organizationName": "Jedi Council",
	"organizationDescription": "An education group.",
	"role": "Padawan",
	"startDate": "Sep 2010",
	"endDate": "",
	"isCurrent": true,
	"location": "A far away galaxy.",
	"roleDescription": "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?",
	"highlights": ["Lifted a rock with the force."]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(127, 1, 0, 5, '{
	"type": "JobExperienceSection",
	"title": "experience",
	"sectionId": 127,
	"sectionPosition": 5,
	"state": "shown",
	"organizationName": "Rebellion",
	"organizationDescription": "An education group.",
	"role": "Pilot",
	"startDate": "Sep 2010",
	"endDate": "",
	"isCurrent": true,
	"location": "A far away galaxy.",
	"roleDescription": "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?",
	"highlights": ["Destroyed a Space a Station.", "Killed an Emperor."]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(128, 1, 0, 6, '{
	"type": "SkillsSection",
	"title": "skills",
	"sectionId": 128,
	"sectionPosition": 6,
	"state": "shown",
	"tags": ["X-Wing Pilot", "Force User"]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(130, 1, 0, 8, '{
	"type": "ProjectsSection",
	"title": "projects",
	"sectionId": 130,
	"sectionPosition": 8,
	"state": "shown",
	"projectTitle": "My project",
	"author":"Someone",
	"projectDate":null,
	"projectImageUrl":"http://img"
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(131, 1, 0, 9, '{
	"type": "CareerInterestsSection",
	"title": "career interests",
	"sectionId": 131,
	"sectionPosition": 9,
	"state": "shown",
	"tags": ["X-Wing Pilot", "Jedi Master"]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(132, 1, 0, 10, '{
	"type": "ProfessionalReferencesSection",
	"title": "professional references",
	"sectionId": 132,
	"sectionPosition": 10,
	"state": "shown",
	"references":
		[{ 
			"pictureUrl": "http://img",
			"firstName": "Leia",
			"lastName": "Organa",
			"description": "Rebel Leader"							
		},
		{
			"pictureUrl": "http://img",
			"firstName": "Obi Wan",
   			"lastName": "Kenobi",
			"description": "Jedi Master"
		}]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into DOCUMENTS.document_sections(id, sectionVersion, documentId, position, jsonDocument, dateCreated, lastModified) values(133, 1, 0, 11, '{
	"type": "PersonalReferencesSection",
	"title": "personal references",
	"sectionId": 133,
	"sectionPosition": 11,
	"state": "shown",
	"references":
		[{ 
			"pictureUrl": "http://img",
			"firstName": "Leia",
			"lastName": "Organa",
			"description": "Rebel Leader"							
		},
		{
			"pictureUrl": "http://img",
			"firstName": "Obi Wan",
   			"lastName": "Kenobi",
			"description": "Jedi Master"
		}]
}',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());


insert into DOCUMENTS.comments(section_Id, section_Version, user_id, comment_Text, last_Modified) values(127, 1, 3, 'Its a me, Mario!', CURRENT_TIMESTAMP());
insert into DOCUMENTS.comments(section_Id, section_Version, user_id, comment_Text, last_Modified) values(128, 1, 2, 'Well played.', CURRENT_TIMESTAMP());

insert into DOCUMENTS.suggestions(section_Id, section_Version, user_id, json_Document, last_Modified) values (127, 1, 0, '{
	"type": "JobExperienceSection",
	"title": "experience",
	"sectionId": 127,
	"sectionPosition": 5,
	"state": "shown",
	"organizationName": "Rebellion",
	"organizationDescription": "An education group.",
	"role": "Pilot",
	"startDate": "Sep 2010",
	"endDate": "",
	"isCurrent": true,
	"location": "A far away galaxy.",
	"roleDescription": "Used an X-Wing to take down a false moon.",
	"highlights": ["Destroyed a Space a Station.", "Killed an Emperor."]
}', CURRENT_TIMESTAMP());
