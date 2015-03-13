# Retrieve the names of the users who commented or added suggestions.
## GET resume/{documentId}/recentUsers?excludeIds=0,3
+ Parameters
	+ documentId (string, `1`) - The id of the document.
+ Request Parameters
	+ excludeIds=0,3... comma separated list of user ids to exclude from the search
	
+ Response 200 (application/json)
```
	[
		{"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"},
		{"userId":3,"firstName":"Some","middleName":null,"lastName":"One"}
	]
``` 