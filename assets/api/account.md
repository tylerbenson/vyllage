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
	+ userId  (string, `1`) - The id of the user.
+ Request Parameters
	+ excludeIds=0,3... comma separated list of user ids to exclude from the search

+ Response 200 (application/json)
```
	[
		{"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"},
		{"userId":3,"firstName":"Some","middleName":null,"lastName":"One"}
	]
```