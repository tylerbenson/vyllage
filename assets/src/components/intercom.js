if(window.location.hostname == 'www.vyllage.com'){
  window.app_id = 'dtqkoq5u';
}else{
  window.app_id = 'g503rj0r';
}

(function() {
    var w = window;
    var ic = w.Intercom;
    if (typeof ic === "function") {
      ic('reattach_activator');
      ic('update', intercomSettings);
    } else {
      var d = document;
      var i = function() {
        i.c(arguments)
      };
      i.q = [];
      i.c = function(args) {
        i.q.push(args)
      };
      w.Intercom = i;

      function l() {
        var s = d.createElement('script');
        s.type = 'text/javascript';
        s.async = true;
        s.src = 'https://widget.intercom.io/widget/'+app_id;
        var x = d.getElementsByTagName('script')[0];
        x.parentNode.insertBefore(s, x);
      }
      if (w.attachEvent) {
        w.attachEvent('onload', l);
      } else {
        w.addEventListener('load', l, false);
      }
    }
})()

window.intercomSettings = {
  name: document.getElementById('header-container') === null ? null : document.getElementById('header-container').getAttribute('name'),
  email: document.getElementById('meta_userInfo_email') === null ? null : document.getElementById('meta_userInfo_email').getAttribute('content'),
  created_at: document.getElementById('meta_userInfo_created_at') === null ? null : document.getElementById('meta_userInfo_created_at').getAttribute('content'),
  app_id: app_id,
  user_id : document.getElementById('meta_userInfo_user') === null ? null : document.getElementById('meta_userInfo_user').content,
  organizationIds : document.getElementById('meta_userInfo_organizationIds') === null ? null : document.getElementById('meta_userInfo_organizationIds').content
};