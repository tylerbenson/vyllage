# Returns the placeholder header in api-response-spec in JSON format.
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


# Saves the tagline. [STUBBED]
## POST /resume/{documentId}/header*
+ Parameters
  + documentId (string, `1`) - The id of the document.
  + sectionId (string, `1`) - The id of the section.
+ Model (application/json)

  {
    "tagline":"This is my tagline."
  }

+ Response 200
