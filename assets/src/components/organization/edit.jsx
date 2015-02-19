var React = require('react');
var OrganizationName = require('./name');
var OrganizationDescription = require('./description');

var OrganizationEdit = React.createClass({   

    getInitialState: function() {
        return {organizationData: this.props.organizationData};
    }, 

    componentDidUpdate: function () {
        this.state.organizationData = this.props.organizationData;
    },

    updateOrganizationName: function (value){
        this.state.organizationData.organizationName = value;
    },

    updateOrganizationDescription: function (value){
        this.state.organizationData.organizationDescription = value;
    },

    updateRole: function (value){
        this.state.organizationData.role = value;
    },

    updateroleDescription: function (value){
        this.state.organizationData.roleDescription = value;
    },

    updateRoleHighlights: function (value){
        this.state.organizationData.highlights = value;
    },

    updateStartDate: function (value){
        this.state.organizationData.startDate = value;
    },

    updateEndDate: function (value){
        this.state.organizationData.endDate = value;
    },

    updateLocation: function (value){
        this.state.organizationData.location = value;
    },

    render: function() {
        return (
            <div className="editable">
                <OrganizationName organizationName={this.props.organizationData.organizationName} updateOrganizationName={this.updateOrganizationName}/>
                <OrganizationDescription organizationDescription={this.props.organizationData.organizationDescription} updateOrganizationDescription={this.updateOrganizationDescription}/>
                
                <div className="edit">
                    <Role role={this.props.organizationData.role} updateRole={this.updateRole}/>
                    <StartDate startDate={this.props.organizationData.startDate} updateStartDate={this.updateStartDate}/>
                    <EndDate endDate={this.props.organizationData.endDate} updateEndDate={this.updateEndDate} />
                    <Location location={this.props.organizationData.location} updateLocation={this.updateLocation} />
                </div>

                <RoleDescription roleDescription={this.props.organizationData.roleDescription} updateroleDescription={this.updateroleDescription}/>
                <Highlights highlights={this.props.organizationData.highlights} updateRoleHighlights={this.updateRoleHighlights} />
            </div>
        );
    }
});

module.exports = OrganizationEdit;