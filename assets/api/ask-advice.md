# Endpoint for loading the request advice form
## GET /resume/{documentId}/ask-advice
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (text/html) - if the document belongs to the current user
  + Returns the HTML document as expected.
+ Response 403 - if the document does not belong to the current user

# Endpoint for submitting the request advice form
## POST /resume/{documentId}/ask-advice
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Request (application/json)
  ++ Body

```
    {
      "csrftoken":"71625f7a-4520-407f-b673-b0ed79e1a678",
 	  "users":[{"userId":4,"firstName":"Carlos","middleName":null,"lastName":"Gagliardi"}],
      "notRegisteredUsers":[{"firstName":"Carlos","lastName":"Gagliardi","email":"carlos.uh@gmail.com"}],
      "subject": "subject text"
      "message": "message of request"
    }
```

+ Response 200 - if the document belongs to the current user
  + Redirect user back to /resume/{documentId}
+ Response 403 - if the document does not belong to the current user


# Get suggestions for adding suggestions
## GET /resume/{documentId}/users?firstNameFilter={firstNameFilter}&lastNameFilter={lastNameFilter}&emailFilter={emailFilter}
+ Parameters
  + firstNameFilter (string, `Ty`) - Filter to be applied to the first name column.
  + lastNameFilter (string, `Ben`) - Filter to be applied to the last name column.
  + emailFilter (string, `tyler@`) - Filter to be applied to the email column.
+ Response 200 (application/json)

```
    {
      "recent" : [
        {"firstName": "Tyler", "lastName": "Benson", "userId": 1},
        {"firstName": "Nathan", "lastName": "Benson", "userId": 2},
        {"firstName": "Nick", "lastName": "Disney", "userId": 3},
        {"firstName": "Keith", "lastName": "Biggs", "userId": 4},
        {"firstName": "Devin", "lastName": "Moncor", "userId": 5}
      ],
      "recommended": [
        {"firstName": "Ashley", "lastName": "Benson", "userId": 6},
        {"firstName": "Doug", "lastName": "Benson", "userId": 7},
        {"firstName": "Duane", "lastName": "Disney", "userId": 8},
        {"firstName": "Rick", "lastName": "Biggs", "userId": 9},
        {"firstName": "Matt", "lastName": "Moncor", "userId": 10}
      ]
    }
```