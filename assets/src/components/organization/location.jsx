var React = require('react');

var Location = React.createClass({

	getInitialState: function() {
		return {location:this.props.location}; 
	},

	componentDidUpdate: function () {
		this.state.location = this.props.location;
	},

	componentWillReceiveProps: function(nextProps) {
		this.state.location = nextProps.location;
	},

	handleChange: function(event) {
		this.setState({location: event.target.value});

		if (this.props.updateLocation) {
			this.props.updateLocation(event.target.value);
		}
	},

	render: function() {
		var location = this.state.location;

		return ( 
			<input type="text" className="location"  placeholder="Location"
			value={location} onChange={this.handleChange}/>
		);
	}
});

module.exports = Location;
