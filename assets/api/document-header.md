# Returns the header.
## GET /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (application/json)
  + Body

    {
    	"firstName":"Luke",
    	"middleName":"V",
    	"lastName":"Skywalker",
    	"tagline":"My tagline.",
    	"address":"Avenida Siempreviva 123",
    	"email":null,
    	"phoneNumber":null,
    	"twitter":null,
    	"linkedIn":null
    }


# Saves the tagline.
## POST /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Body (application/json)

  {
    "tagline":"This is my tagline."
  }

+ Response 200
