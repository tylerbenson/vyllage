# Get job openings based on the user's resume.
## GET /resume/{documentId}/job-offers

+ Parameters
	++ documentId (string, `1`) - The id of the document

+ Response 200 (application/json)
```
[
  {
    "organizationId": 1,
    "jobType": "fulltime",
    "jobExperience": "fresh_graduate",
    "salary": 1000,
    "location": "Buenos Aires",
    "requiresRelocation": false,
    "remote": false,
    "dateCreated": "2016-02-17T01:40:34",
    "lastModified": "2016-02-17T01:40:34",
    "company": "Google",
    "role": "Corporate Operations Engineer, IT Support Technician",
    "description": "Technical support for a technology company is a big task. As a Corporate Operations Engineer you are the go-to person for Googlers' computer hardware and software needs, providing front line user support for all of Google's internal tools and technologies. You troubleshoot, respond to inquiries and find solutions to technical challenges. Beyond the day-to-day, you improve the Googler user experience by contributing to longer term projects and documentation efforts. You are highly technical and are comfortable problem solving with multiple operating systems (like OS X, Linux, Windows) and a range of devices (including desktops/laptops, phone systems, video conferencing and various wireless devices). You occasionally partner with various teams including security, networking and infrastructure. You're a fast learner and great communicator who can support the IT needs of global offices of all sizes and Googlers of varying technical backgrounds.\nYou improve the front-line user experience by providing on-demand user support for Google's corporate users, resources, tools and applications while also contributing to longer-term projects.",
    "siteWide": false
  },
  {
    "organizationId": null,
    "jobType": null,
    "jobExperience": null,
    "salary": 0,
    "location": "Chesterfield, VA, US",
    "requiresRelocation": false,
    "remote": false,
    "dateCreated": "2016-02-15T08:07:49",
    "lastModified": "2016-02-15T08:07:49",
    "company": "Chesterfield County, VA",
    "role": "Performance Consultant",
    "description": "Proficiency in computer <b>software</b> including online authoring tools and Microsoft Office products is required. Bachelor's degree in Adult Education, Instructional...",
    "siteWide": false
  }
]
``` 