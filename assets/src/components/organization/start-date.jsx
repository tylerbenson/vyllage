var React = require('react');

var StartDate = React.createClass({

    getInitialState: function() {
        return {startDate: this.props.startDate}; 
    },

    componentDidUpdate: function () {
        this.state.startDate = this.props.startDate;
    },

    componentWillReceiveProps: function(nextProps) {
        this.state.startDate = nextProps.startDate;
    },

    handleChange: function(event) {
        this.setState({startDate: event.target.value});

        if (this.props.updateStartDate) {
            this.props.updateStartDate(event.target.value);
        }
    },

    render: function() {
        var startDate = this.state.startDate;

        return (
            <input type="text" className="start-date" placeholder="Start Date"
                 value= {startDate}  onChange={this.handleChange}/>
        );
    }
});

module.exports = StartDate;