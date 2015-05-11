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

## POST /link/share-document 
+Body (application/json)
++ DocumentType: currently there's only a single document type.
```
	{
  		"documentId":0,
  		"documentType":"resume"
	}
```
+ Response 200
++ String, encoded link to the document.

## GET /access-shared-document/{encodedDocumentLink}
+ Parameters
++ encodedDocumentLink (string, `www.vyllage.com/link/access-shared-document/Ph5c-0vx3FlslrH...`) - The link to the document.

Redirects to Facebook to login. If the user doesn't exist in our database it's created using his profile.
Once the user it's logged in he is redirected to the document he wanted to access.

