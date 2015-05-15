To be able to test login with facebook using a shared link you must do the following.

Edit your OS host file, ie /etc/hosts for linux, adding the following line
127.0.1.1 local.vyllage.com

Once you have done this you'll be able to use local.vyllage.com in your browser instead of localhost, this is required because Facebook does not allow using localhost.
 