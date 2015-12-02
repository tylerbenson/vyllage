#XML Job Search Feed - Indeed Publisher

### Attribution
You must use the following attribution when displaying Indeed's job search results.
Copy the code below and paste it near the results on your web page:

```
<span id=indeed_at>
	<a href="http://www.indeed.com/">jobs
	</a> by 
 		<a href="http://www.indeed.com/" title="Job Search">
  			<img src="http://www.indeed.com/p/jobsearch.gif" style="border: 0;vertical-align: middle;" alt="Indeed job search">
  		</a>
</span>
```

### Click Tracking
Your HTML markup must reference a javascript file for click tracking support.
Copy the code below and paste it into the <head> section of your web page:

```
<script type="text/javascript" src="http://gdc.indeed.com/ads/apiresults.js"> </script>
```

If your site is accessed via https, you may use:
```
<script type="text/javascript" src="https://gdc.indeed.com/ads/apiresults.js"> </script>
```

### DNS caching
To maintain redundancy across multiple geographic regions, your application should regularly update the api.indeed.com IP address from our DNS servers.

### Channels
Append channel names to API requests to view individual performance for multiple websites.
param.widgetLabel
None
Append channel name to API requests in the following format:
&chnl=xxxxxxxxxxxxxxx

## Sample Request
Sample format of an xml request:

** http://api.indeed.com/ads/apisearch?publisher={id}&q=java&l=austin%2C+tx&sort=&radius=&st=&jt=&start=&limit=&fromage=&filter=&latlong=1&co=us&chnl=&userip=1.2.3.4&useragent=Mozilla/%2F4.0%28Firefox%29&v=2 ** 

publisher: Publisher ID. Your publisher ID. This is assigned when you register as a publisher.

format: Format. Which output format of the API you wish to use. The options are "xml" and "json". If omitted or invalid, the XML format is used.
callback Callback. The name of a javascript function to use as a callback to which the results of the search are passed. This only applies when format=json. For
security reasons, the callback name is restricted letters, numbers, and the underscore character.

q: Query. By default terms are ANDed. To see what is possible, use our advanced search page to perform a search and then check the url for the q value.

l: Location. Use a postal code or a "city, state/province/region" combination.

sort: Sort. Sort by relevance or date. Default is relevance.

radius: Radius. Distance from search location ("as the crow flies"). Default is 25.

st: Site type. To show only jobs from job boards use "jobsite". For jobs from direct employer websites use "employer".

jt: Job type. Allowed values: "fulltime", "parttime", "contract", "internship", "temporary".

start: Start. Start results at this result number, beginning with 0. Default is 0.

limit: Limit. Maximum number of results returned per query. Default is 10

Fromage. Number of days back to search.

highlight: Highlight. Setting this value to 1 will bold terms in the snippet that are also present in q. Default is 0.

filter: Filter duplicate results. 0 turns off duplicate job filtering. Default is 1.

latlong: If latlong=1, returns latitude and longitude information for each job result. Default is 0.

co:  Search within country specified. Default is us See below for a complete list of supported countries.

chnl: Channel Name: Group API requests to a specific channel

userip: The IP number of the end-user to whom the job results will be displayed. This field is required.
 
v: Version. Which version of the API you wish to use. All publishers should be using version 2. Currently available versions are 1 and 2. This parameter
is required.

useragent: The User-Agent (browser) of the end-user to whom the job results will be displayed. This can be obtained from the "User-Agent" HTTP request header
from the end-user. This field is required.


### Sample XML Search Response - Version 2
The URL above would result in the following feed:

```
<?xml version="1.0" encoding="UTF-8" ?>
<response version="2">
    <query>java</query>
    <location>austin, tx</location>
    <dupefilter>true</dupefilter>
    <highlight>false</highlight>
    <totalresults>547</totalresults>
    <start>1</start>
    <end>10</end>
    <radius>25</radius>
    <pageNumber>0</pageNumber>
    <results>
        <result>
            <jobtitle>Java Developer</jobtitle>
            <company>XYZ Corp.</company>
            <city>Austin</city>
            <state>TX</state>
            <country>US</country>
            <formattedLocation>Austin, TX</formattedLocation>
            <source>Dice</source>
            <date>Mon, 02 Aug 2010 16:21:00 GMT</date>
            <snippet>looking for an object-oriented Java Developer... Java Servlets, HTML, JavaScript,
            AJAX, Struts, Struts2, JSF) desirable. Familiarity with Tomcat and the Java...</snippet>
            <url>http://www.indeed.com/viewjob?jk=12345&indpubnum=8343699265155203</url>
            <onmousedown>indeed_clk(this,'0000');</onmousedown>
            <latitude>30.27127</latitude>
            <longitude>-97.74103</longitude>
            <jobkey>12345</jobkey>
            <sponsored>false</sponsored>
            <expired>false</expired>
            <formattedLocationFull>Austin, TX</formattedLocationFull>
            <formattedRelativeTime>11 hours ago</formattedRelativeTime>
        </result>
        ...
    </results>
</response>
``` 

formattedLocation and formattedLocationFull will often be identical. The exact values differ based on country and the data we have available.
radius is optional; it will only be included when appropriate.


* Note that the ordering of response fields is not guaranteed
### Sample XML Response - Obsolete Version 1
Omitting v=2 or using any other value in the URL above will result in an obsolete version error. For historical purposes, see below a sample of the output from the
obsolete version 1:

```
<?xml version="1.0" encoding="UTF-8" ?>
<response>
    <query>java</query>
    <location>austin, tx</location>
    <dupefilter>true</dupefilter>
    <highlight>false</highlight>
    <totalresults>547</totalresults>
    <start>1</start>
    <end>10</end>
    <results>
        <result>
            <jobtitle>Java Developer</jobtitle>
            <company>XYZ Corp.</company>
            <city>Austin</city>
            <state>TX</state> <!-- see "State Information" section for meaning in non-US searches -->
            <country>US</country>
            <source>Dice</source>
            <date>Sat, 28 Nov 2015 10:41:41 GMT</date>
            <snippet>looking for an object-oriented Java Developer... Java Servlets, HTML, JavaScript,
            AJAX, Struts, Struts2, JSF) desirable. Familiarity with Tomcat and the Java...</snippet>
            <url>http://www.indeed.com/viewjob?jk=12345&indpubnum=1758907727091006</url>
            <onmousedown>indeed_clk(this,'0000');</onmousedown>
            <latitude>30.27127</latitude>
            <longitude>-97.74103</longitude>
            <jobkey>12345</jobkey>
        </result>
        ...
    </results>
</response>
```

* Note that the ordering of response fields is not guaranteed

## Get Job API

There is now an additional API available for looking up individual jobs using the jobkey. The output fields are identical to the search API. However, the response does
not include the various search parameters such as query or radius. The query parameters are:
publisher Publisher ID. 

jobkeys: Job keys. A comma separated list of job keys specifying the jobs to look up. This parameter is required.

v: Version. Which version of the API you wish to use. All publishers should be using version 2. Currently available versions are 1 and 2. This parameter is
required.

format: Format. Which output format of the API you wish to use. The options are "xml" and "json". If omitted or invalid, the XML format is used.

Get Job XML API Sample

Sample URL: http://api.indeed.com/ads/apigetjobs?publisher={id}&jobkeys=5e50b56a7e69073c&v=2. Sample response:
```
<response version="2">
    <results>
        <result>
            <jobtitle>Sales Account Executive</jobtitle>
            <company>Indeed</company>
            <city>Stamford</city>
            <state>CT</state>
            <country>US</country>
            <formattedLocation>Stamford, CT</formattedLocation>
            <source>Indeed</source>
            <date>Tue, 03 Aug 2010 14:00:47 GMT</date>
            <snippet>
            Indeed is looking for strategic Account Executives to help in the expansion of our Stamford, CT location. We are
            in the process of interviewing sales candidates who have 2-5 years of sales experience and who have experience
            generating new business and growing existing accounts. A strong candidate will have excellent communication
            skills, consistent work ethic and a desire to be a part of the...
            </snippet>
            <url>http://www.indeed.com/rc/clk?jk=0123456789abcdef</url>
            <onmousedown>indeed_clk(this, '');</onmousedown>
            <latitude>41.057693</latitude>
            <longitude>-73.54395</longitude>
            <jobkey>0123456789abcdef</jobkey>
            <sponsored>false</sponsored>
            <expired>false</expired>
            <formattedLocationFull>Stamford, CT 06902</formattedLocationFull>
            <formattedRelativeTime>6 days ago</formattedRelativeTime>
        </result>
    </results>
</response>
```

Note that the ordering of response fields is not guaranteed 

## Supported Countries

|Country | Country code|
|:-------|-------------|
|United States | us |
|Argentina | ar |
|Australia | au |
|Austria | at | 
|Bahrain | bh |
|Belgium | be |
|Brazil | br |
|Canada | ca |
|Chile | cl |
|China | cn | 
|Colombia | co |
|Czech Republic | cz |
|Denmark | dk |
|Finland | fi |
|France | fr |
|Germany | de |
|Greece | gr |
|Hong Kong | hk |
|Hungary | hu |
|India | in |
|Indonesia | id |
|Ireland | ie |
|Israel | il |
|Italy | it |
|Japan | jp | 
|Korea | kr |
|Kuwait | kw |
|Luxembourg | lu |
|Malaysia | my | 
|Mexico | mx |
|Netherlands | nl |
|New Zealand | nz |
|Norway | no |
|Oman | om |
|Pakistan | pk |
|Peru | pe |
|Philippines | ph |
|Poland | pl |
|Portugal | pt |
|Qatar | qa |
|Romania | ro | 
|Russia | ru |
|Saudi Arabia | sa |
|Singapore | sg |
|South Africa | za |
|Spain | es |
|Sweden | se |
|Switzerland | ch | 
|Taiwan | tw |
|Turkey | tr |
|United Arab Emirates | ae |
|United Kingdom | gb |
|Venezuela | ve |


## State Information
The meaning of the state element varies for each supported country and conforms to the following conventions:

|Country | Administrative Division | Code Type |
|--------|-------------------------|-----------|
Austria  | State (Bundesland)      |standard abbreviations| 
Australia | State/Territory | ISO 3166-2
Belgium | Province | ISO 3166-2
Brazil | State (Estado) | ISO 3166-2
Canada | Province/Territory | ISO 3166-2
France | Region | FIPS 10-4
Germany | State (Bundesland) | ISO 3166-2
India | State | ISO 3166-2
Ireland | County | ISO 3166-2
Italy | Region | standard abbreviations
Mexico | State (Estado) | ISO 3166-2
Netherlands | Province | ISO 3166-2
Spain | Province | ISO 3166-2
Switzerland | Canton | ISO 3166-2
United Kingdom | Country | ISO 3166-2
United States | State| ISO 3166-2


