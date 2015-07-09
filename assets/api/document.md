# Retrieve the names of the users who commented or added suggestions.
## GET resume/{documentId}/recent-users?excludeIds=0,3
+ Parameters
	++ documentId (string, `1`) - The id of the document.
+ Request Parameters
	++ excludeIds=0,3... comma separated list of user ids to exclude from the search
	
+ Response 200 (application/json)
```
	[
		{"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"},
		{"userId":3,"firstName":"Some","middleName":null,"lastName":"One"}
	]
``` 

# Deletes documents from several users.
## DELETE /document/delete?userIds=1,2
+ Request Parameters
	++ userIds=0,3 - comma separated list of user ids
+ Response 204

This endpoint is not intended for consumption from the UI. Only admins can use this endpoint.

# Returns a PDF file conversion of the selected document
## GET /resume/{documentId}/file/pdf
+ Parameters
	++ documentId (string, `1`) - The id of the document.
+ Response 200 (application/pdf)


# Returns an object mapping document ids by document type 
## GET /document/user/{userId}/document-type/{documentType}
+ Parameters
	++ userId (string, `1`) - The id of the user.
	++ userId (string, `RESUME`) - The document type (resume).

```
{"resume":[0, 1, 2, n]}
```

# Returns an object mapping document ids by document type for the logged in user
## GET /document/user/document-type/{documentType}
+ Parameters
	++ userId (string, `RESUME`) - The document type (resume).

```
{"resume":[0, 1, 2, n]}
```

# Returns all the document permissions the currently logged in user has
## GET /document/permissions
This endpoint is intended for internal use.

```
	[
		{
			"documentId":0,
			"userId":3,
			"dateCreated":[2015,7,9,0,56,59,451000000],
			"lastModified":[2015,7,9,0,56,59,451000000],
			"expirationDate":null
		}
	]
```

# Creates new permission for the document and user
## POST document/{documentId}/permissions/user/{userId}
+ Response 200


# Revokes access to a document for the given user
## DELETE document/{documentId}/permissions/user/{userId}
+ Response 200
