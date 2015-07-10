# Get all account setting
## GET /account/setting
+ Response (application/json)	

```
	[
		{"accountSettingId":0,"userId":0,"name":"emailUpdates","value":"weekly","privacy":"public"},	
		{"accountSettingId":1,"userId":0,"name":"firstName","value":null,"privacy":"public"},
		{"accountSettingId":2,"userId":0,"name":"middleName","value":null,"privacy":"public"},
		{"accountSettingId":3,"userId":0,"name":"lastName","value":null,"privacy":"private"},
		{"accountSettingId":4,"userId":0,"name":"address","value":"Avenida Siempreviva 123","privacy":"public"}
	]
```

# Updates all settings 
## PUT /account/setting
+ Response (application/json)	

```
	[
		{"accountSettingId":0,"userId":0,"name":"emailUpdates","value":"weekly","privacy":"public"},	
		{"accountSettingId":1,"userId":0,"name":"firstName","value":null,"privacy":"public"},
		{"accountSettingId":2,"userId":0,"name":"middleName","value":null,"privacy":"public"},
		{"accountSettingId":3,"userId":0,"name":"lastName","value":null,"privacy":"private"},
		{"accountSettingId":4,"userId":0,"name":"address","value":"Avenida Siempreviva 123","privacy":"public"}
	]
```

# Get account setting
## GET /account/setting/{setting}
+ setting: (String, `firstName`) The name of the setting. 
+ Response (application/json)

```
	{
		"accountSettingId":1, 
		"userId":0,
		"name":"firstName",
		"value":"Luke",
		"privacy":"public"
	}
```

# Save account setting 
## PUT /account/setting/{setting}
+ Body (application/json)

```
	{
		"accountSettingId":1, 
		"userId":0,
		"name":"firstName",
		"value":"Luke",
		"privacy":"public"
	}
```
+ Response 200

# Get values for an specific setting.
## GET /account/setting/{setting}/values
+ setting: (String, `firstName`) The name of the setting. (emailUpdates | role | organization | privacy) 
+ Response (application/json)

Email Updates.
```
	["weekly","biweekly","monthly","never"]
```

For Users with Student role, only the following values are available.
```
	["STUDENT","ALUMNI"]
```

Privacy settings. 
```
 ["private","public","organization"]
```

### Other settings: 

+ emailUpdates (weekly|biweekly|monthly|never) 
+ **Role and Organization can only be saved using the administration pages.**  


# Returns document permissions plus user information.
## GET /account/document/permissions
```
	[
		{
			"documentId":0,
			"userId":3,
			"firstName":"Mario",
			"middleName":null,
			"lastName":"Mario",
			"tagline":"Awesome adventurous plumber.",
			"dateCreated":[2015,7,9,0,56,59,451000000],
			"expirationDate":null
		}
	]
```
