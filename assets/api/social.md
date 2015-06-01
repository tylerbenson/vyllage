How to login/singup to Facebook or any other social account.

For each social account tree forms are required, signin, signup and connect.
Note that it's not possible to use REST for this, the user must go to the social account site to accept the connection.

##Sign In Form
Used to login to vyllage using a social account. 

Example:

<form name="fb_signin" id="fb_signin" th:action="@{/signin/facebook}" method="POST">
    <input type="hidden" name="_csrf" th:value="${_csrf.token}"></input>
	<input type="hidden" name="scope" value="email"></input>
	<button type="submit">Facebook</button>
</form>

Notice the **scope** field, this is used to request permissions to the user, like this:  
<input type="hidden" name="scope" value="publish_stream,user_photos,offline_access"></input>

## Sign Up
Currently there's no support for user sign up in the application, 
instead we use the sign in form to automatically create an account when the user accepts the request to connect his social account.

## Connect Form
Connect is used to connect an existing Vyllage account to a social account.
The user must be logged in to do this.

Example:

<form action="/connect/facebook" method="POST">
    <input type="hidden" name="scope" value="email" />
    <div class="formInfo">
      <p>You aren't connected to Facebook yet. Click the button to connect this application with your Facebook account.</p>
    </div>
    <p>
      <button type="submit">Connect to Facebook</button>
    </p>
</form> 
  