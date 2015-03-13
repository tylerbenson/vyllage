var React = require('react');

var EndDate = React.createClass({

	getInitialState: function() {
		return {endDate: this.props.endDate}; 
	},

	componentDidUpdate: function () {
		this.state.endDate = this.props.endDate;
	},

	componentWillReceiveProps: function(nextProps) {
		this.state.endDate = nextProps.endDate;
	},

	handleChange: function(event) {
		this.setState({endDate: event.target.value});

		if (this.props.updateEndDate) {
			this.props.updateEndDate(event.target.value);
		}
	},

	render: function() {
		var endDate = this.state.endDate;

		return (
			<input type="text" className="end-date" placeholder="Etart Date"
			value={endDate}  onChange={this.handleChange}/>
		);
	}
});

module.exports = EndDate;