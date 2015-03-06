# Save phone number 
## POST /account/phoneNumber/{phoneNumber}*
+ Response 200

# Save frequency to receive emails
## POST /account/emailUpdates/{emailUpdates}* 
+ Parameters  
	+ emailUpdates (weekly|biweekly|monthly|never)

+ Response 200

# Save Graduation Date
## POST /account/graduationDate/*
Model (application/json) without {}

"MMMM-yyyy"
  
+ Response 200 