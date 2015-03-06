# Save phone number 
## POST /account/phoneNumber/{phoneNumber}*
+ Response 200

# Save frequency to receive emails
## POST /account/emailUpdates/{emailUpdates}* 
+ Parameters  
	+ emailUpdates (WEEKLY|BIWEEKLY|MONTHLY|NEVER)

+ Response 200

# Save Graduation Date
## POST /account/graduationDate/*
Model (application/json) without {}

"yyyy-MM-dd'T'HH:mm:ss.SSS"
  
+ Response 200 