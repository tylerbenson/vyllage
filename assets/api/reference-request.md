# Create a reference request.
## POST /reference/request?otherUserId=1
+ Parameters
  + otherUserId (string, `1`) - The id of the user we want to add as reference.
  
Response 202.

# Accept a reference request from another user
## POST /reference/accept
Accepts a reference request from another user and creates the reference in the other user's resume.

Request Body

```
{
    "userId": 0,
    "dateCreated": "2015-11-03T21:33:50",
    "userName": "Mailinator Email",
    "otherUserId": 8,
    "type": "WebReferenceRequestNotification"
}
```

Response 202.

# Reject and delete a reference request.
## DELETE /reference/reject

Currently, this is the same as *DELETE /notification/request-reference*, in the future it could be used to send an explanation to the requesting user about the rejection. 

Request Body

```
{
    "userId": 0,
    "dateCreated": "2015-11-03T21:33:50",
    "userName": "Mailinator Email",
    "otherUserId": 8,
    "type": "WebReferenceRequestNotification"
}
```

Response 204.