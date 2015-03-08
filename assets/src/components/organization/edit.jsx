var React = require('react');
var OrganizationName = require('./name');
var OrganizationDescription = require('./description');
var Role = require('./role');
var StartDate =require('./start-date');
var EndDate = require('./end-date');
var Location = require('./location');
var RoleDescription =require('./role-description');
var Highlights = require('./highlights');
var Buttons = require('./buttons');

var OrganizationEdit = React.createClass({   

    getInitialState: function() {
        return {organizationData: this.props.organizationData};
    }, 

    componentDidUpdate: function () {
        this.state.organizationData = this.props.organizationData;
    },

    valid: function () {
        if(this.state.organizationData !== "" && this.state.organizationData !== undefined) {
            if(this.state.organizationData.organizationName.trim() || 
                this.state.organizationData.organizationDescription.trim() || 
                this.state.organizationData.role.trim() || 
                this.state.organizationData.roleDescription.trim() ||
                this.state.organizationData.highlights.trim() || 
                this.state.organizationData.location.trim() ||  
                this.state.organizationData.startDate.trim() || 
                this.state.organizationData.highlights.trim()) {
                return true;
            }  else return false;
        } else return false;
    },

    save: function () {
        if(this.props.save){
            this.props.save();
        }
    },

    cancel: function () {
        if(this.props.cancel){
            this.props.cancel();
        }
    },

    updateOrganizationName: function (value){
        this.state.organizationData.organizationName = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateOrganizationDescription: function (value){
        this.state.organizationData.organizationDescription = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateRole: function (value){
        this.state.organizationData.role = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateroleDescription: function (value){
        this.state.organizationData.roleDescription = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateRoleHighlights: function (value){
        this.state.organizationData.highlights = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateStartDate: function (value){
        this.state.organizationData.startDate = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateEndDate: function (value){
        this.state.organizationData.endDate = value;
        this.setState({organizationData: this.state.organizationData});
    },

    updateLocation: function (value){
        this.state.organizationData.location = value;
        this.setState({organizationData: this.state.organizationData});
    },

    render: function() {
        return (
            <div className="editable">
                <div>
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

                <Buttons ref="buttonContainer" save={this.save} cancel={this.cancel} valid={this.valid()}/>
            </div>
        );
    }
});

module.exports = OrganizationEdit;