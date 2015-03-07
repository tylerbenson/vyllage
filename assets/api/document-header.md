# Returns the header.
## GET /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (application/json)
  + Body

    {
      "firstName": "Nathan",
      "middleName": "M",
      "lastName": "Benson",
      "tagline": "Technology Enthusiast analyzing, building, and expanding solutions"
    }


# Saves the tagline.
## POST /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Model (application/json)

  {
    "tagline":"This is my tagline."
  }

+ Response 200
