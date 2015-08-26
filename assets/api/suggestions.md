# Returns all the suggestions for a particular section.
## GET /resume/{documentId}/section/{sectionId}/suggestion
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
+ Response 200 (application/json)
  + Body
```
[
  {
    "suggestionId": 1,
    "sectionId": 128,
    "sectionVersion": 1,
    "lastModified": "2015-08-26T00:19:43",
    "userId": 0,
    "userName": "Luke Skywalker",
    "avatarUrl": "https://secure.gravatar.com/avatar/631164c3aeb35618622fe67602ce5da8",
    "documentSection": {
      "title": "skills",
      "sectionId": 128,
      "sectionVersion": 1,
      "type": "SkillsSection",
      "state": "shown",
      "sectionPosition": 6,
      "numberOfComments": 1,
      "lastModified": "2015-08-25T16:46:01",
      "tags": [
        "X-Wing Pilot!",
        "Force User!"
      ]
    }
  }
]
```

# Creates a new suggestion for a section.
## POST /resume/{documentId}/section/{sectionId}/suggestion
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
+ Request
  + Body

SectionId, SectionVersion, UserId and documentSection with the suggested changes are required. 
UserName, avatarUrl can be null on POST, they are provided by the backend to show who created the suggestion.
LastModified, is not required, can be null.

 ```
[
  {
    "suggestionId": null,
    "sectionId": 128,
    "sectionVersion": 1,
    "lastModified": "2015-08-26T00:19:43",
    "userId": 0,
    "userName": null,
    "avatarUrl": null,
    "documentSection": {
      "title": "skills",
      "sectionId": 128,
      "sectionVersion": 1,
      "type": "SkillsSection",
      "state": "shown",
      "sectionPosition": 6,
      "numberOfComments": 1,
      "lastModified": "2015-08-25T16:46:01",
      "tags": [
        "X-Wing Pilot!",
        "Force User!"
      ]
    }
  }
]
```

# Updates a suggestion
## PUT /resume/{documentId}/section/{sectionId}/suggestion/{suggestionId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
  + suggestionId  (string, `1`) - The id of the suggestion to be updated.
+ Request
  + Body

SectionId, SectionVersion, UserId and documentSection with the suggested changes are required. 
UserName, avatarUrl can be null on PUT, they are provided by the backend to show who created the suggestion.
LastModified, is not required, can be null.

 ```
[
  {
    "suggestionId": 1,
    "sectionId": 128,
    "sectionVersion": 1,
    "lastModified": "2015-08-26T00:19:43",
    "userId": 0,
    "userName": null,
    "avatarUrl": null,
    "documentSection": {
      "title": "skills",
      "sectionId": 128,
      "sectionVersion": 1,
      "type": "SkillsSection",
      "state": "shown",
      "sectionPosition": 6,
      "numberOfComments": 1,
      "lastModified": "2015-08-25T16:46:01",
      "tags": [
        "X-Wing Pilot!",
        "Force User!"
      ]
    }
  }
]
```

