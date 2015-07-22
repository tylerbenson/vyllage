# Returns a list of sections from the specified document in JSON format. If the section can't be found then returns code 404 with the following response:
## GET /resume/{documentId}/section*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 404 {error:"Reason"}

## GET /resume/{documentId}/section*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (application/json)
  + Body
```
    [
      {
    "title": "education",
    "sectionId": 125,
    "type": "EducationSection",
    "state": "hidden",
    "sectionPosition": 3,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T22:35:54",
    "organizationName": "Keller Graduate School of Management",
    "organizationDescription": "Management School.",
    "role": "Masters of Project Management",
    "roleDescription": "Lorem ipsum dolor sit amet",
    "startDate": "Sep 2010",
    "endDate": "Sep 2012",
    "location": "Portland, Oregon",
    "isCurrent": false,
    "highlights": [
      "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
    ]
  },
  {
    "title": "skills",
    "sectionId": 126,
    "type": "SkillsSection",
    "state": null,
    "sectionPosition": 4,
    "numberOfComments": 1,
    "lastModified": "2015-07-22T22:35:54",
    "tags": [
      "rendis",
      "doloribus",
      "asperiores",
      "repellat."
    ]
  }
]
```

# Retrieves the specified section. 
## GET /resume/{documentId}/section/{sectionId}*
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Response 200 (application/json)
  + Body
```
  {
    "title": "personal references",
    "sectionId": 133,
    "type": "PersonalReferencesSection",
    "state": "shown",
    "sectionPosition": 11,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "references": [
      {
        "pictureUrl": "http://img",
        "name": "Leia Organa",
        "description": "Rebel Leader"
      },
      {
        "pictureUrl": "http://img",
        "name": "Obi Wan Kenobi",
        "description": "Jedi Master"
      }
    ]
  }
```

# Updates the section from the request body (JSON). Returns the saved section.
## PUT /resume/{documentId}/section/{sectionId}*
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Body
```
{
    "title": "summary",
    "sectionId": 123,
    "type": "SummarySection",
    "state": "shown",
    "sectionPosition": 1,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T22:35:54",
    "description": "This is my goal statement."
  }
```
+ Response 200


# Creates the section from the request body (JSON). Returns the saved section.
## POST /resume/{documentId}/section/*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Request
  + Body
  
```
    {
    "title": "skills",
    "sectionId": 126,
    "type": "SkillsSection",
    "state": null,
    "sectionPosition": 4,
    "numberOfComments": 1,
    "lastModified": "2015-07-22T22:35:54",
    "tags": [
      "rendis",
      "doloribus",
      "asperiores",
      "repellat."
    ]
  }  
```
+ Response 200


# Deletes the section.
## DELETE /resume/{documentId}/section/{sectionId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Response 200

#Returns all the comments from a section.
## GET /resume/{documentId}/section/{sectionId}/comment
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section. 
+ Response 200 (application/json)
  + Body
 ```
	[
	  {
	     "commentId":2,
	     "otherCommentId":null,
	     "sectionId":123,
	     "sectionVersion":1,
	     "userId":0,
	     "commentText":"aaaa",
	     "lastModified":"2015-07-23T02:28:33",
	     "userName":"Luke Skywalker",
	     "avatarUrl":"http://www.gravatar.com/avatar/631164c3aeb35618622fe67602ce5da8"
	  }
	]
```

#Save Comment for a Section
## POST resume/{documentId}/section/{sectionId}/comment
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section. 
+Body  
```
	{	
		"commentId":null,
		"otherCommentId":null,
		"sectionId":124,
		"sectionVersion":1,
		"userId":3,
		"commentText":"Its a me, Mario!",
		"userName":"Mario Mario"
	}
```

 + Response 200

#Save Comment in response to another comment
## POST resume/{documentId}/section/{sectionId}/comment/{commentId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section. 
  + commentId (string, `1`) - The id of the comment. 
+Body  

```
	{	
		"commentId":null,
		"otherCommentId":2,
		"sectionId":124,
		"sectionVersion":1,
		"userId":3,
		"commentText":"Its a me, Mario!",
		"userName":"Mario Mario"
	}
```

 + Response 200

# Change a document's sections positions
## PUT resume/{documentId}/section-order
+ Parameters 
  + documentId (string, `1`) - The id of the document.
+Body, sectionIds in their intended order. The position will be derived from their position in the array.  

```	
		[126, 125, 124, 123]
```
+ Response 200 (application/json)

## Section reference:

General notes.
* Title can be whatever you want it to be.

### SummarySection
```
{
    "title": "summary",
    "sectionId": 129,
    "type": "SummarySection",
    "state": "shown",
    "sectionPosition": 7,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "description": "Rebellion fighter."
}
```

### JobExperienceSection
```
{
    "title": "experience",
    "sectionId": 127,
    "type": "JobExperienceSection",
    "state": "shown",
    "sectionPosition": 5,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "organizationName": "Rebellion",
    "organizationDescription": "An education group.",
    "role": "Pilot",
    "roleDescription": "But I must explain to you?",
    "startDate": "Sep 2010",
    "endDate": null,
    "location": "A far away galaxy.",
    "isCurrent": true,
    "highlights": [
      "Destroyed a Space a Station.",
      "Killed an Emperor."
    ]
}
```

### ProjectSection
```
{
    "title": "projects",
    "sectionId": 130,
    "type": "ProjectsSection",
    "state": "shown",
    "sectionPosition": 8,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "projectTitle": "My project",
    "author": "Someone",
    "projectDate": null,
    "projectImageUrl": "http://img"
}
```

### CareerInterestsSection

```
{
    "title": "career interests",
    "sectionId": 131,
    "type": "CareerInterestsSection",
    "state": "shown",
    "sectionPosition": 9,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "tags": [
      "X-Wing Pilot",
      "Jedi Master"
    ]
}
```

### ProfessionalReferencesSection
```
{
    "title": "professional references",
    "sectionId": 132,
    "type": "ProfessionalReferencesSection",
    "state": "shown",
    "sectionPosition": 10,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "references": [
      {
        "pictureUrl": "http://img",
        "name": "Leia Organa",
        "description": "Rebel Leader"
      },
      {
        "pictureUrl": "http://img",
        "name": "Obi Wan Kenobi",
        "description": "Jedi Master"
      }
    ]
}
```

### PersonalReferencesSection
```
{
    "title": "personal references",
    "sectionId": 133,
    "type": "PersonalReferencesSection",
    "state": "shown",
    "sectionPosition": 11,
    "numberOfComments": 0,
    "lastModified": "2015-07-22T19:35:52",
    "references": [
      {
        "pictureUrl": "http://img",
        "name": "Leia Organa",
        "description": "Rebel Leader"
      },
      {
        "pictureUrl": "http://img",
        "name": "Obi Wan Kenobi",
        "description": "Jedi Master"
      }
    ]
}
```

### EducationSection
```
{
    "title": "experience",
    "sectionId": 124,
    "type": "EducationSection",
    "state": "shown",
    "sectionPosition": 2,
    "numberOfComments": 1,
    "lastModified": "2015-07-22T22:35:54",
    "organizationName": "DeVry Education Group",
    "organizationDescription": "An education group.",
    "role": "Manager, Local Accounts",
    "roleDescription": "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?",
    "startDate": "Sep 2010",
    "endDate": null,
    "location": "Portland, Oregon",
    "isCurrent": true,
    "highlights": [
      "I was in charge of..."
    ]
}
```

### SkillsSection
```
{
    "title": "skills",
    "sectionId": 126,
    "type": "SkillsSection",
    "state": null,
    "sectionPosition": 4,
    "numberOfComments": 1,
    "lastModified": "2015-07-22T22:35:54",
    "tags": [
      "rendis",
      "doloribus",
      "asperiores",
      "repellat."
    ]
}
```

### AchievementsSection
```
TBD.
```

