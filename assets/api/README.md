**Editor**

**GET** */resume/{documentId}/section*

Returns a list of sections from the specified document in JSON format. (See api-response-spec)

**GET** */resume/{documentId}/section/{sectionId}*

Returns the requested section in JSON format. If the section can't be found then returns code 404 with the following response:

{"error":"DocumentSection with id '2' not found."}

**POST** */resume/{documentId}/section/{sectionId}*

Updates the section from the request body (JSON).

**POST** */resume/{documentId}/section*

Creates the section in the request body (JSON).

Returns the saved section.

**DELETE** */resume/{documentId}/section/{sectionId}*

Deletes the section.

**GET** */resume/{documentId}/header*

The placeholder header in api-response-spec in JSON format.