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
## GET /resume/{documentId}/file/pdf?style=styleName
+ Parameters
	++ documentId (string, `1`) - The id of the document.
	++ styleName (string, `1`) - Style to use in the pdf. Optional parameter. 
+ Response 200 (application/pdf)


# Returns a PNG file conversion of the selected document
## GET /resume/{documentId}/file/png?style=styleName&width=width&height=height
+ Parameters
	++ documentId (string, `1`) - The id of the document.
	++ styleName (string, `1`) - Style to use in the pdf. Optional parameter. 
	++ width (int, `1`) - The width of the image.
	++ height (int, `1`) - The height of the image.
+ Response 200 (image/png)

# Get pdf styles
## GET /resume/file/pdf/styles
Returns
```
{"default","narrow"}
```

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

# Returns the last modification date of the user's document.
## GET /document/user/{userId}/modified-date
+ Parameters
	++ userId (string, `1`) - The id of the user.
	
Returns: the number of milliseconds since the epoch of 1970-01-01T00:00:00Z.
	

