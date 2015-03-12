# Retrieve names for a list of user ids.
## POST /account/names
+ Body 
	+ [0, 1, 2] 
+ Response 200 (application/json)
```
   [	
      {"userId":0, "firstName":"Luke","middleName":"V","lastName":"Skywalker"},
      {"userId":1,"firstName":null,"middleName":null,"lastName":null},
      {"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"}
   ]
```
 
# Retrieve a list of advisors names for an specific user.
## GET account/advisors/{userId}
+ Parameters
	+ userId  (string, `1`) - The id of the user.
	
+ Response 200 (application/json)
```
	[
		{"userId":2,"firstName":"Deana","middleName":null,"lastName":"Troi"},
		{"userId":3,"firstName":"Some","middleName":null,"lastName":"One"}
	]
```