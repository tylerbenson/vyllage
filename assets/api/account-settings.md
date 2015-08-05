# Get all account settings
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
	[{
		"accountSettingId":1, 
		"userId":0,
		"name":"firstName",
		"value":"Luke",
		"privacy":"public"
	}]
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

Avatar settings.
```
["gravatar", "facebook"]
```

Receive advice setting
```
 ["true","false"]
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
			"tagline":"Awesome adventurous plumber."
		}
	]
```
# Get new email after change.
## GET /account/setting/newEmail

After the user changes his email address the value is stored under the name **newEmail** so the frontend can query the new value to show in a banner or something.

```
	[{
		"accountSettingId":1, 
		"userId":0,
		"name":"newEmail",
		"value":"mynew@email.com",
		"privacy":"private"
	}]
```

# Check if a user is connected to a certain social network
## GET /account/social/{network}/is-connected
+ network: (String, `facebook`), valid networks: facebook.
+ Response: true | false.
 


