var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');

var AdminStore = Reflux.createStore({
	  listenables: require('./actions'),
	  init: function () {
		  this.admin = {
				  roles:[]
		  };
	  },
	  onGetRoles: function(){
		  var url = endpoints.getRoles;
		  
		  request
		    .get(url)
		    .end(function(err, res) {
		    	if(res.ok) 
			    	this.admin.roles = res.body;
		    	else
		    		this.admin.roles = [];
			  
		    }.bind(this));
	  },
	  getInitialState: function () {
		    return this.admin;
	  }
});

	
module.exports = AdminStore;