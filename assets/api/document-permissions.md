# Returns all the document permissions the currently logged in user has
## GET /document/permissions

```
	[
		{
			"documentId":0,
			"userId":3,
			"dateCreated":"2015-07-15T21:18:38",
			"lastModified":"2015-07-15T21:18:38",
			"expirationDate":null,
			"userName":"Luke Skywalkwer",
			"tagline":"My tagline."
			"allowGuestComments":true
		}
	]
```

# Creates new permission for the document and user
## POST document/{documentId}/permissions/user/{userId}
+ Body 
```
	{
		"userId":36,
		"documentId":903,
		"allowGuestComments":true
	}
	
```

+ Response 202


# Revokes access to a document for the given user
## DELETE document/{documentId}/permissions/user/{userId}
+ Response 204
