Note: notifications, currently once created can only be deleted by the recipient so if I ask for feedback only the receiving user can see and delete it. 

# Retrieve all notifications.
## GET /notification/all

```
{
  "WebReferenceRequestNotification": [
    {
      "userId": 0,
      "dateCreated": "2015-11-03T11:43:29",
      "userName": "Mailinator Email",
      "otherUserId": 8
    }
  ],
  "WebCommentNotification": [
    {
      "userId": 0,
      "dateCreated": "2015-11-03T11:43:29",
      "userName": "Mario Mario",
      "otherUserId": 3,
      "commentId": 0,
      "sectionTitle": "experience"
    }
  ],
  "WebFeedbackRequestNotification": [
    {
	    "userId": 8,
    	"dateCreated": "2015-11-03T11:43:29",
    	"userName": "Luke Skywalker",
    	"otherUserId": 0,
    	"resumeId": 0
    }
  ]
}
```

# Retrieve all comment notifications
## GET /notification/comment	

Comment notifications are generated whenever a user posts a comment.

```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Mario Mario",
    "otherUserId": 3,
    "commentId": 0,
    "sectionTitle": "experience"
  }
]
```

# Delete a comment notification.
## DELETE /notification/comment 
Body (application/json):
```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Mario Mario",
    "otherUserId": 3,
    "commentId": 0,
    "sectionTitle": "experience"
  }
]
```
Response 204.

# Get all feedback requests notifications
## GET /notification/request-feedback

These are generated automatically when a user request feedback from an existing user.


```
[
  {
    "userId": 8,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Luke Skywalker",
    "otherUserId": 0,
    "resumeId": 0
  }
]
```

# Delete a feedback request. 
## DELETE /notification/request-feedback

```
[
  {
    "userId": 8,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Luke Skywalker",
    "otherUserId": 0,
    "resumeId": 0
  }
]
```
Response 204.

# Get reference requests
## GET /notification/request-reference

```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Mailinator Email",
    "otherUserId": 8
  }
]
```

# Delete a feedback request. 
## DELETE /notification/request-reference
Deletes a reference request.

```
[
  {
    "userId": 0,
    "dateCreated": "2015-11-03T11:43:29",
    "userName": "Mailinator Email",
    "otherUserId": 8
  }
]
```

Response 204.