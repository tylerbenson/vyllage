var React = require('react');

var OrganizationName = React.createClass({

	getInitialState: function() {
		return {organizationName: this.props.organizationName}; 
	},

	componentDidUpdate: function () {
		this.state.organizationName = this.props.organizationName;
	},

	componentWillReceiveProps: function(nextProps) {
		this.state.organizationName = nextProps.organizationName;
	},

	handleChange: function(event) {
		this.setState({organizationName: event.target.value});

		if (this.props.updateOrganizationName) {
			this.props.updateOrganizationName(event.target.value);
		}
	},

	render: function() {
		var organizationName = this.state.organizationName; 

		return (
			<div className="edit">
				<input type="text" className="organization-name" placeholder="Organization Name" 
					value={organizationName} onChange={this.handleChange}/>
			</div>
		);
	}
});

module.exports = OrganizationName;