var React = require('react');

var RoleDescription = React.createClass({

    getInitialState: function() {
        return {roleDescription:this.props.roleDescription}; 
    },

    componentDidUpdate: function () {
        this.state.roleDescription = this.props.roleDescription;
    },

    handleChange: function(event) {
        this.setState({roleDescription: event.target.value});

        if (this.props.updateroleDescription) {
            this.props.updateroleDescription(event.target.value);
        }
    },

    render: function() {
        var roleDescription = this.state.roleDescription; 

        return (
            <div className="edit">
                <textarea className="role-description"  placeholder="Role Description"
                value={roleDescription} onChange={this.handleChange}></textarea>
            </div>
        );
    }
});

module.exports = RoleDescription;