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
+ Request (application/json)
  ++ Body

```
    {
 	  "users":[{"userId":4,"firstName":"aName","middleName":null,"lastName":"aLastName"}],
      "notRegisteredUsers":[{"firstName":"aName","lastName":"aLastName","email":"anemail@gmail.com"}],
      "subject": "subject text"
      "message": "message of request",
      "allowGuestComments":false
    }
```

+ Response 200 - if the document belongs to the current user
  + Redirect user back to /resume/{documentId}
+ Response 403 - if the document does not belong to the current user


# Get suggestions for adding suggestions
## GET /resume/users?firstNameFilter={firstNameFilter}&lastNameFilter={lastNameFilter}&emailFilter={emailFilter}&excludeIds=0,1
+ Parameters
  + firstNameFilter (string, `Ty`) - Filter to be applied to the first name column.
  + lastNameFilter (string, `Ben`) - Filter to be applied to the last name column.
  + emailFilter (string, `tyler@`) - Filter to be applied to the email column.
  + excludeIds=0,3... comma separated list of user ids to exclude from the search

+ Response 200 (application/json)

```
{
  "recent": [
    {
      "userId": 2,
      "address": null,
      "email": "deana1@vyllage.com",
      "phoneNumber": null,
      "twitter": null,
      "linkedIn": null,
      "firstName": "Deana1",
      "middleName": null,
      "lastName": "Troi",
      "avatarUrl": "https://secure.gravatar.com/avatar/1ea123da4938b9a7cb5553eee600c337",
      "tagline": "",
      "advisor": true
    },
    {
      "userId": 3,
      "address": null,
      "email": "mario@toadstool.com",
      "phoneNumber": null,
      "twitter": null,
      "linkedIn": null,
      "firstName": "Mario",
      "middleName": null,
      "lastName": "Mario",
      "avatarUrl": "https://secure.gravatar.com/avatar/bfbc7470bc5302be09837669de926d09",
      "tagline": "Awesome adventurous plumber.",
      "advisor": false
    }
  ],
  "recommended": [
    {
      "userId": 4,
      "address": null,
      "email": "maquiavelo@vyllage.com",
      "phoneNumber": null,
      "twitter": null,
      "linkedIn": null,
      "firstName": "Mac",
      "middleName": null,
      "lastName": "",
      "avatarUrl": "https://secure.gravatar.com/avatar/a87f0c0d9172324b263bc6ca09419358",
      "tagline": null,
      "advisor": true
    }
  ]
}
```