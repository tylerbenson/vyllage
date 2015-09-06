# Returns all the section advices for a particular section.
## GET /resume/{documentId}/section/{sectionId}/advice
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
+ Response 200 (application/json)
  + Body
```
[
  {
    "sectionAdviceId": 1,
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

# Creates a new section advice for a section.
## POST /resume/{documentId}/section/{sectionId}/advice
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
+ Request
  + Body

SectionId, SectionVersion, UserId and documentSection with the suggested changes are required. 
UserName, avatarUrl can be null on POST, they are provided by the backend to show who created the section advice.
LastModified, is not required, can be null.

 ```
[
  {
    "sectionAdviceId": null,
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

# Updates a section advice
## PUT /resume/{documentId}/section/{sectionId}/advice/{sectionAdviceId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId  (string, `1`) - The id of the section.
  + sectionAdviceId  (string, `1`) - The id of the section advice to be updated.
+ Request
  + Body

SectionId, SectionVersion, UserId and documentSection with the suggested changes are required. 
UserName, avatarUrl can be null on PUT, they are provided by the backend to show who created the section advice.
LastModified, is not required, can be null.

 ```
[
  {
    "sectionAdviceId": 1,
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

