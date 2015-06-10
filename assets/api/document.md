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

This endpoint is not intended for consumption from the UI. (Currently it can only be called from 127.0.0.1, might be replaced with role validation)

# Returns a PDF file conversion of the selected document
## /resume/{documentId}/file/pdf
+ Parameters
	++ documentId (string, `1`) - The id of the document.
+ Response 200 (application/pdf)