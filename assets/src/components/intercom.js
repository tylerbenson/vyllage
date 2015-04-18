var moment = require('moment');

window.intercomSettings = {
    // TODO: The current logged in user's full name
    name: document.getElementById('header-container').getAttribute('name'),
    // TODO: The current logged in user's email address.
    email: document.getElementById('meta_email').getAttribute('content'),
    // TODO: The current logged in user's sign-up date as a Unix timestamp.
    created_at: 1429371686,
    app_id: "vyllage"
};
(function(){var w=window;var ic=w.Intercom;if(typeof ic==="function"){ic('reattach_activator');ic('update',intercomSettings);}else{var d=document;var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};w.Intercom=i;function l(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://widget.intercom.io/widget/dtqkoq5u';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);}if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})()