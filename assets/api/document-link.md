## POST /link/create 
Admin users only.
+Body (application/json)

```
	{
		"name":"Juana",
		"email":"juana@vyllage.com",
		"documentId":1,
		"documentType":"resume",
		"expirationDate":"2015-02-24T20:18:49.304"
	}
```

+ Response
++ String: '"/link/advice/" + safeString', where safeString is a RSA encoded object (DocumentLink) containing the following: 
+++ userId, the user that will be logged in into the system.
+++ generatedPassword, a random 20 character password.
+++ documentId, the document the user will be able to comment on.
+++ documentType, "resume" for now.

