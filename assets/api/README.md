# Returns a list of sections from the specified document in JSON format. (See api-response-spec)

## GET /resume/{documentId}/section*
+ Response 200 (application/json)
  [{
      "type": "freeform",
      "title": "career goal",
      "sectionId": 123,
      "sectionPosition": 1,
      "state": "shown",
      "description": "this is my goal statement."
    },
    {
      "type": "experience",
      "title": "experience",
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
    }]


# Returns the requested section in JSON format. If the section can't be found then returns code 404 with the following response:

## GET /resume/{documentId}/section/{sectionId}*
+ Response 200 (application/json)
  {
    "type": "freeform",
    "title": "career goal",
    "sectionId": 123,
    "sectionPosition": 1,
    "state": "shown",
    "description": "this is my goal statement."
  }  



#Updates the section from the request body (JSON). Returns the saved section.
## POST /resume/{documentId}/section/{sectionId}*
+ Response 200


# Deletes the section.
## DELETE /resume/{documentId}/section/{sectionId}
+ Response 200


# The placeholder header in api-response-spec in JSON format.
## GET /resume/{documentId}/header*
+ Response 200 (application/json)
  {
    "firstName": "Nathan",
    "middleName": "M",
    "lastName": "Benson",
    "tagline": "Technology Enthusiast analyzing, building, and expanding solutions"
  }


