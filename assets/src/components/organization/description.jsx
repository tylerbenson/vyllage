var React = require('react');

var OrganizationDescription = React.createClass({  

   getInitialState: function() {
        return {organizationDescription: this.props.organizationDescription}; 
    },

    componentDidUpdate: function () {
        this.state.organizationDescription = this.props.organizationDescription;
    },

    componentWillReceiveProps: function(nextProps) {
        this.state.organizationDescription = nextProps.organizationDescription;
    },

    handleChange: function(event) {
        this.setState({organizationDescription: event.target.value});

        if (this.props.updateOrganizationDescription) {
            this.props.updateOrganizationDescription(event.target.value);
        }
    },

    render: function() {
        var organizationDescription = this.state.organizationDescription; 

        return (
            <div className="edit">
                <textarea className="organization-description"  placeholder="Organization Description"
                    value={organizationDescription} onChange={this.handleChange}></textarea>
            </div>
        );
    }
});

module.exports = OrganizationDescription;