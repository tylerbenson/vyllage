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

    [
      {
        "type": "freeform",
        "title": "Career Goal",
        "documentId": 1,
        "sectionId": 123,
        "sectionPosition": 1,
        "state": "shown",
        "description": "this is my goal statement."
      },
      {
        "type": "organization",
        "title": "Experience",
        "documentId": 1,
        "sectionId": 124,
        "sectionPosition": 2,
        "state": "shown",
        "organizationName": "DeVry Education Group",
        "organizationDescription": "Blah Blah Blah.",
        "role": "Manager, Local Accounts",
        "numberOfComments":1
        "startDate": "September 2010",
        "endDate": "",
        "isCurrent": true,
        "location": "Portland, Oregon",
        "roleDescription": "Blah Blah Blah",
        "highlights": "I was in charge of..."
      }
    ]


# Retrieves the specified section. This will rarely be used as getting all sections preferred.
## GET /resume/{documentId}/section/{sectionId}*
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Response 200 (application/json)
  + Body

    {
      "type": "freeform",
      "title": "Career Goal",
      "documentId": 1,
      "sectionId": 123,
      "sectionPosition": 1,
      "state": "shown",
      "description": "this is my goal statement."
    }


#Updates the section from the request body (JSON). Returns the saved section.
## PUT /resume/{documentId}/section/{sectionId}*
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Response 200

#Creates the section from the request body (JSON). Returns the saved section.
## POST /resume/{documentId}/section/*
+ Parameters
  + documentId (string, `1`) - The id of the document.
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
 
	[{	
		"commentId":0,
		"otherCommentId":null,
		"sectionId":124,
		"sectionVersion":1,
		"userId":3,
		"commentText":"Its a me, Mario!",
		"userName":"Mario Mario"
	}]

#Save Comment for a Section
## POST {documentId}/section/{sectionId}/comment
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section. 
+Body  

	{	
		"commentId":null,
		"otherCommentId":null,
		"sectionId":124,
		"sectionVersion":1,
		"userId":3,
		"commentText":"Its a me, Mario!",
		"userName":"Mario Mario"
	}
 + Response 200

#Save Comment in response to another comment
## POST {documentId}/section/{sectionId}/comment/{commentId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section. 
  + commentId (string, `1`) - The id of the comment. 
+Body  

	{	
		"commentId":null,
		"otherCommentId":2,
		"sectionId":124,
		"sectionVersion":1,
		"userId":3,
		"commentText":"Its a me, Mario!",
		"userName":"Mario Mario"
	}
 + Response 200

