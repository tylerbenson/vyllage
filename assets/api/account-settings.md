# Get all account setting
## GET /account/setting*
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
## GET /account/setting/{setting}*
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
## PUT /account/setting/{setting}*
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

### Other settings: 

+ emailUpdates (weekly|biweekly|monthly|never) 

For Role and Organization it's only possible to change their privacy settings.