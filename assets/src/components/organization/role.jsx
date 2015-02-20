var React = require('react');

var Role = React.createClass({

    getInitialState: function() {
        return {role: this.props.role}; 
    },

    componentDidUpdate: function () {
        this.state.role = this.props.role;
    },

    handleChange: function(event) {
        this.setState({role: event.target.value});

        if (this.props.updateRole) {
            this.props.updateRole(event.target.value);
        }
    },

    render: function() {
        var role = this.state.role;

        return (
            <input type="text" className="role" placeholder="Enter your role" 
                value= {role}  onChange={this.handleChange} />
        );
    }
});

module.exports = Role;