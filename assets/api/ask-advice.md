# Endpoint for loading the request advice form
## GET /resume/get-feedback
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Response 200 (text/html) - if the document belongs to the current user
  + Returns the HTML document as expected.
+ Response 403 - if the document does not belong to the current user

# Endpoint for submitting the request advice form
## POST /resume/get-feedback
+ Parameters
  + documentId (string, `1`) - The id of the document.
+ Request (application/json)
  ++ Body

```
    {
 	  "users":[{"userId":4,"firstName":"aName","middleName":null,"lastName":"aLastName"}],
      "notRegisteredUsers":[{"firstName":"aName","lastName":"aLastName","email":"anemail@gmail.com"}],
      "subject": "subject text"
      "message": "message of request"
    }
```

+ Response 200 - if the document belongs to the current user
  + Redirect user back to /resume/{documentId}
+ Response 403 - if the document does not belong to the current user


# Get suggestions for adding suggestions
## GET /resume/users?firstNameFilter={firstNameFilter}&lastNameFilter={lastNameFilter}&emailFilter={emailFilter}
+ Parameters
  + firstNameFilter (string, `Ty`) - Filter to be applied to the first name column.
  + lastNameFilter (string, `Ben`) - Filter to be applied to the last name column.
  + emailFilter (string, `tyler@`) - Filter to be applied to the email column.
+ Response 200 (application/json)

```
    {
      "recent" : [
        {"firstName": "Tyler", "middleName":"Middle", "lastName": "Benson", "userId": 1},
        {"firstName": "Nathan", "middleName":"Middle", "lastName": "Benson", "userId": 2},
        {"firstName": "Nick", "middleName":"Middle", "lastName": "Disney", "userId": 3},
        {"firstName": "Keith", "middleName":"Middle", "lastName": "Biggs", "userId": 4},
        {"firstName": "Devin", "middleName":"Middle", "lastName": "Moncor", "userId": 5}
      ],
      "recommended": [
        {"firstName": "Ashley", "middleName":"Middle", "lastName": "Benson", "userId": 6},
        {"firstName": "Doug", "middleName":"Middle", "lastName": "Benson", "userId": 7},
        {"firstName": "Duane", "middleName":"Middle", "lastName": "Disney", "userId": 8},
        {"firstName": "Rick", "middleName":"Middle", "lastName": "Biggs", "userId": 9},
        {"firstName": "Matt", "middleName":"Middle", "lastName": "Moncor", "userId": 10}
      ]
    }
```