# Returns the header.
## GET /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (application/json)
  + Body

```
    {
    	"firstName":"Luke",
    	"middleName":"V",
    	"lastName":"Skywalker",
    	"tagline":"My tagline.",
    	"address":"Avenida Siempreviva 123",
    	"email":null,
    	"phoneNumber":null,
    	"twitter":null,
    	"linkedIn":null,
    	"owner"true
    }
```

# Saves the tagline.
## PUT /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Body (application/json)

```
  {
    "tagline":"This is my tagline."
  }
```
+ Response 200

Or Error 400 if the tagline fails the validation:
+ Body

```
{
  "firstName": "Luke",
  "middleName": "V",
  "lastName": "Skywalker",
  "tagline": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
  "address": "Avenida Siempreviva 123",
  "email": "user@vyllage.com",
  "phoneNumber": "",
  "twitter": "",
  "linkedIn": null,
  "owner": true,
  "avatarUrl": "https://secure.gravatar.com/avatar/631164c3aeb35618622fe67602ce5da8",
  "error": "Tagline must be shorter than 100 characters.",
  "inValid": true
}
```

