var OrganizationMain = React.createClass({   

    render: function() {
        return (
            <div className="nonEditable">

                <div className="main">
                    <p className="organization-name">
                        {this.props.organizationData.organizationName} 
                    </p>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="organization-description">
                            {this.props.organizationData.organizationDescription}
                        </p>
                    </div>
                </div>

                <div className="main">
                    <p className="title">
                        {this.props.organizationData.role}
                    </p>
                    <p className="start-date">
                        {this.props.organizationData.startDate}
                    </p>
                    <p className="end-date">
                        {this.props.organizationData.endDate}
                    </p>
                    <p className="location">
                        {this.props.organizationData.location}
                    </p>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="role-description">
                            {this.props.organizationData.roleDescription}
                        </p>
                    </div>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="highlights">
                            {this.props.organizationData.highlights}
                        </p>
                    </div>
                </div>

            </div>
        );
    }
});

var OrganizationName = React.createClass({

    getInitialState: function() {
        return {organizationName:''}; 
    },

    componentDidUpdate: function () {
        this.state.organizationName = this.props.organizationName;
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

var OrganizationDescription = React.createClass({  

   getInitialState: function() {
        return {organizationDescription:''}; 
    },

    componentDidUpdate: function () {
        this.state.organizationDescription = this.props.organizationDescription;
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

var Role = React.createClass({

    getInitialState: function() {
        return {role:''}; 
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

var StartDate = React.createClass({

    getInitialState: function() {
        return {startDate:''}; 
    },

    componentDidUpdate: function () {
        this.state.startDate = this.props.startDate;
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

var EndDate = React.createClass({

    getInitialState: function() {
        return {endDate:''}; 
    },

    componentDidUpdate: function () {
        this.state.endDate = this.props.endDate;
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

var Location = React.createClass({

    getInitialState: function() {
        return {location:''}; 
    },

    componentDidUpdate: function () {
        this.state.location = this.props.location;
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


var RoleDescription = React.createClass({

    getInitialState: function() {
        return {roleDescription:''}; 
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

var Highlights = React.createClass({

    getInitialState: function() {
        return {highlights:''}; 
    },

    componentDidUpdate: function () {
        this.state.highlights = this.props.highlights;
    },

    handleChange: function(event) {
        this.setState({highlights: event.target.value});

        if (this.props.updateRoleHighlights) {
            this.props.updateRoleHighlights(event.target.value);
        }
    },

    render: function() {
        var highlights = this.state.highlights; 

        return (
             <div className="edit">
                <textarea className="highlights" placeholder="Highlights"
                value={highlights} onChange={this.handleChange}></textarea>
            </div>
        );
    }
});


var Buttons = React.createClass({   

    saveHandler: function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.props.save) {
            this.props.save();
        }
    },    

    cancelHandler: function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.props.cancel) {
            this.props.cancel();
        }
    },

    render: function() {
        return (
            <div className="buttons-container">
                <button className="save-btn" onClick={this.saveHandler}>save</button>
                <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
            </div>
        );
    }
});

var OrganizationEdit = React.createClass({   

    getInitialState: function() {
        return {organizationData: ''};
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

var ArticleContent = React.createClass({  

    getInitialState: function() {
        return {
                isMain: true,
                organizationData: ''
            };
    }, 

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.refs.editContainer.state.organizationData);
        }
        this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            
            this.refs.mainContainer.getDOMNode().style.display="block";
            this.refs.editContainer.getDOMNode().style.display="none";

            this.refs.buttonContainer.getDOMNode().style.display="none";

            this.state.isMain=true ;
        }

        return false;
    },

     goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.organizationData));
            this.setState({
                organizationData :data,
                isMain: false
             });

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="article-content" onClick={this.goToEditMode}>

                <OrganizationMain ref="mainContainer" organizationData={this.props.organizationData}/>
                <OrganizationEdit ref="editContainer" organizationData={this.props.organizationData} updateOrganizationName={this.updateOrganizationName}/>
                <Buttons ref="buttonContainer" save={this.save} cancel={this.cancel}/>
            </div>
        );
    }
});

var ArticleControlls = React.createClass({
    render: function() {
        return (
            <div className="article-controll">
                <div className="article-controll-btns">
                    <div className="u-pull-left">
                        <a href="" className="suggestions">suggestions</a>
                        <span className="suggestions-count count">2</span>
                    </div>
                    <div className=" u-pull-left">
                        <a href="" className="comments">comments</a>
                        <span className="suggestions-count count">3</span>
                    </div>
                </div>
            </div>
        );
    }
});

var CommentsBlog = React.createClass({
    render: function() {
        return (
            <div className="comments-content">
                <div className="comment-list-block">
                    <div className="comment-person-info">
                        <img className="u-pull-left" src="images/comment-person.png" width="40" height="40" />
                        <p className="u-pull-left name">Richard Zielke</p>
                        <p className="u-pull-left date">2 hrs ago</p>
                    </div>
                    <div className="comment-body">
                        <p className="">
                            I like what you are saying here about your experience with DeVry. You have done an excellent job of outlining your successes.
                        </p>
                    </div>
                    <div className="comment-ctrl">
                        <div className="u-pull-right">
                            <ul className="comment-ctrl-list">
                                <li className="comment-ctrl-btn"><a href="#">thank</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">hide</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">reply</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div className="comment-divider"></div>
            </div>
        );
    }
});