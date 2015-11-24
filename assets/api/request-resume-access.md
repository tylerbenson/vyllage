Using GET /notification/resume-access-request you can get all access requests notifications, check notifications.md

# Create resume access request
## POST /resume/{documentId}/access-request
+ Parameters
	++ documentId (string, `1`) - The id of the document.
+ Request Body
```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T21:33:50",
    "userName": "Mailinator Email",
    "otherUserId": 8,
    "type": "WebResumeAccessRequestNotification"
  }
]
```

Response 202. 

# Accept resume access request
## POST /resume/access-request

+ Request Body

```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T21:33:50",
    "userName": "Mailinator Email",
    "otherUserId": 8,
    "type": "WebResumeAccessRequestNotification"
  }
]
```
Response 202.

# Reject resume access request
## DELETE /resume/access-request

+ Request Body

```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T21:33:50",
    "userName": "Mailinator Email",
    "otherUserId": 8,
    "type": "WebResumeAccessRequestNotification"
  }
]
```

Response 204.