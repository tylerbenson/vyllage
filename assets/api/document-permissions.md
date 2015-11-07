# Returns all the document permissions the currently logged in user has
## GET /account/document/permissions

```
[
  {
    "documentId": 0,
    "userId": 3,
    "firstName": "Mario",
    "middleName": null,
    "lastName": "Mario",
    "tagline": "Awesome adventurous plumber.",
    "dateCreated": "2015-11-07T00:28:06"
  }
]
```

# Returns all the document permissions the currently logged in user has
## GET /document/permissions
For internal use from Accounts.

```
[
  {
    "documentId": 0,
    "userId": 3,
    "dateCreated": [
      2015,
      11,
      7,
      0,
      28,
      6,
      7000000
    ],
    "lastModified": [
      2015,
      11,
      7,
      0,
      28,
      6,
      7000000
    ],
    "expirationDate": null,
    "allowGuestComments": true
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
