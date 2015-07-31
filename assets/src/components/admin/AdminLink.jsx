var React = require('react');
var Reflux = require('reflux');
var adminStore = require('./store');
var actions = require('./actions');

var AdminLink = React.createClass({
  mixins: [Reflux.connect(adminStore, 'admin')],
  componentWillMount: function () {
    actions.getRoles();
  },
  render: function() {
  		
  		var found = false;
		for (var i = 0; i < this.state.admin.roles.length && !found; i++) {
		  if (this.state.admin.roles[i] === "ADMIN") 
		    found = true;
		}
  		
  		if(found)
  			return (
				<a href={"/admin/users"} className="flat print button">
	        	<i className="ion-hammer"></i>
	        	<span>Admin</span>
	     		</a>
			);
  		else
			return (<span></span>);
			//not sure what else to put here? with an empty string it complains that it's not a valid ReactComponent.
	}

});

module.exports = AdminLink;