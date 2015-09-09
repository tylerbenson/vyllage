# Properties:
The following properties are not saved in the DB with the comment, they are calculated by the appication. 
They must be included in the JSON object since they cannot be made optional and without them the parser will fail. They can be sent as null instead.

+ userName.
+ avatarUrl.

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
		"userName":null
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
		"userName":null,
		"avatarUrl":null
	}
```

+ Response 200
 
# Delete a Comment from a Section
## DELETE resume/{documentId}/section/{sectionId}/comment/{commentId}
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
  + commentId (string, `1`) - The id of the comment.
+Body
The comment must be included since we use it to determine if the user is deleting his own comment.
```
	{
		"commentId":3,
		"otherCommentId":null,
		"sectionId":129,
		"sectionVersion":1,
		"userId":0,
		"commentText":"Hello!\nWorld?",
		"lastModified":"2015-09-08T03:49:39",
		"userName":null,
		"avatarUrl":null
	}
```

+ Response 202