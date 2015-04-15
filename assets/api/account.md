# Retrieve names for a list of user ids.
## GET /account/names?userIds=1,2
+ RequestParam   
	+ userIds=0,1,2... comma separated list of user ids to search for
+ Response 200 (application/json)
```
   [	
      {"userId":0, "firstName":"Luke","middleName":"V","lastName":"Skywalker"},
      {"userId":1,"firstName":null,"middleName":null,"lastName":null},
      {"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"}
   ]
```
 
# Retrieve a list of advisors names for an specific user.
## GET account/{userId}/advisors?excludeIds=0,1
+ Parameters
	++ userId  (string, `1`) - The id of the user.
+ Request Parameters
	++ excludeIds=0,3... comma separated list of user ids to exclude from the search

+ Response 200 (application/json)
```
	[
		{"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"},
		{"userId":3,"firstName":"Some","middleName":null,"lastName":"One"}
	]
```

# Retrieve a list of contact data from several users
## GET /account/contact?userIds=1,2...n
+ RequestParam   
	++ userIds=0,1,2... comma separated list of user ids to search for
+ Response 200 (application/json)
```
	[
		{"userId":0,"address":"Avenida Siempreviva 123","email":null,"phoneNumber":null,"twitter":null,"linkedIn":null}
	]
```

# Delete a user account and remove his documents.
## DELETE /account/delete
+ Parameters
	++ Body:
```
		{"value" : _csfr.token}
``` 
+ Response 200 (txt/html) - user-deleted.html
The csfr token value is required to use the service in Documents.


 

