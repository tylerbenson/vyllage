# URLs

## /swagger-ui.html
Html view of ALL endpoints.

## /v2/api-docs
Huge Json view of all endpoints.

## /swagger-resources
Lists all of the swagger resources and configured versions.

### Notes.

#### @EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
For some reason when Swagger is added it also drags the HypermediaAutoConfiguration in Spring Boot's autoconfiguration, this conflicts with ours, every other configuration and Swagger's Jackson ObjectMapper bean.

See [answer](http://stackoverflow.com/questions/31307883/springfox-dependency-breaking-my-spring-context) in Stackoverflow.

> I had the same exact issue. I was finally able to narrow it down to the root cause, which in my case turned out to be what @Dilip pointed out above.
>
> Somehow, HypermediaSupport was getting enabled in my project when I include springfox dependencies, even though I didn't have the @EnableHypermediaSupport annotation explicitly in my project. I think Spring was enabling it automagically because I had @EnableAutoConfiguration in my main java configuration class.
>
> I was able to overcome this issue by excluding 'HypermediaAutoConfiguration' via the @EnableAutoConfiguration(exclude = {HypermediaAutoConfiguration.class}) annotation in my java config class.
>
> After I did this step, the exception went away. Hope this helps.
