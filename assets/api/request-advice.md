These are the api details I need for request advice feature.

# Endpoint for submitting the request advice form
## POST /request-advice/{userId}
+ Request (application/json)
  
  {
    "to": ["list of recipients"]
    "subject": "subject text"
    "message": "message of request"
  }

+ Response 200


# Get suggestions for adding suggestions
## GET /request-advice/{userId}/suggestions

+ Response 200 (application/son)

  {
    "recent" : [
      {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
      {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
      {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
      {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
      {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"}
    ],
    "recommended": [
      {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
      {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
      {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
      {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
      {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"}
    ]
  }
