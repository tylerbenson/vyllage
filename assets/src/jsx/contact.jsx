var Social = React.createClass({

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    editPrivateLink: function() {
        if(!this.state.editMode){
            this.refs.twitterEdit.getDOMNode().style.display="block" ;
            this.refs.twitterMain.getDOMNode().style.display="none" ;

            this.refs.linkedinEdit.getDOMNode().style.display="block" ;
            this.refs.linkedinMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.twitterEdit.getDOMNode().style.display="none";
            this.refs.twitterMain.getDOMNode().style.display="";

            this.refs.linkedinEdit.getDOMNode().style.display="none" ;
            this.refs.linkedinMain.getDOMNode().style.display="block" ;

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateTwitter: function (link) {
        this.state.data.link=link;
    },

    updatelinkedin: function (link) {
        this.state.data.link=link;
    },

    render: function() {
		return (
            <div className="social info-blog">
                <p className="info-title">Social</p>
                <div className="edit-btn-cont u-pull-left" onClick={this.editPrivateLink}>
                    <button className="edit-btn"></button>
                </div>
                <div className="contact-data-cont u-pull-left">
                    <p className="contact-link" ref="twitterMain">
                        twitter: <a href="" className="soc-link">
                             {this.props.data.twitter}
                        </a>
                    </p>
                    <SocialEditField ref="twitterEdit" data={this.props.data} updateTwitter={this.updateTwitter}/>
                    <p className="contact-link" ref="linkedinMain"> 
                        linkedin: <a href="" className="soc-link">
                            {this.props.data.linkedin}
                        </a>
                    </p>
                    <SocialEditField ref="linkedinEdit" data={this.props.data} updatelinkedin={this.updatelinkedin}/>
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.props.data.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
		);
    }
});

var SocialEditField = React.createClass({    

    getInitialState: function() {
        return {link: this.props.data.link}; 
    },

    componentDidUpdate: function () {
        this.state.link = this.props.data.link;
    },

    handleChange: function(event) {
        this.setState({link: event.target.value});

        if (this.props.updatePrivateLink) {
            this.props.updatePrivateLink(event.target.value);
        }
    },

    render: function() {
        var link = this.state.link;

        return (
            <div className="contact-link edit">
                <input type="text" className="soc-link edit" value={link}  onChange={this.handleChange}/>
            </div>
        );
    }
});

var Contact = React.createClass({

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    edit: function() {
        if(!this.state.editMode){
            this.refs.emailEdit.getDOMNode().style.display="block" ;
            this.refs.emailMain.getDOMNode().style.display="none" ;

            this.refs.cellEdit.getDOMNode().style.display="block" ;
            this.refs.cellMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.emailEdit.getDOMNode().style.display="none";
            this.refs.emailMain.getDOMNode().style.display="";

            this.refs.cellEdit.getDOMNode().style.display="none" ;
            this.refs.cellMain.getDOMNode().style.display="block" ;

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateEmail: function (link) {
        this.state.data.link=link;
    },

    updateCell: function (link) {
        this.state.data.link=link;
    },

    render: function() {
        return (
            <div className="social info-blog">
                <p className="info-title">Contact</p>
                <div className="edit-btn-cont u-pull-left" onClick={this.edit}>
                    <button className="edit-btn"></button>
                </div>
                <div className="contact-data-cont u-pull-left">
                    <p className="contact-link" ref="emailMain">
                        email: <a href="" className="soc-link">
                             {this.props.data.email}</a>
                    </p>
                    <SocialEditField ref="emailEdit" data={this.props.data} updateEmail={this.updateEmail}/>
                    <p className="contact-link" ref="cellMain">
                        cell: <a href="" className="soc-link">
                            {this.props.data.cell}</a>
                    </p>
                    <SocialEditField ref="cellEdit" data={this.props.data} updateCell={this.updateCell}/>
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.props.data.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
        );
    }
});

var LocationMain = React.createClass({    

    render: function() {
        return (
            <div className="info-blog location-blog">
                <p className="info-title">Location</p>
                <div className="edit-btn-cont u-pull-left">
                    <button className="edit-btn"></button>
                </div>
                <div className="contact-data-cont location-contact-data-cont u-pull-left">
                    <p className="contact-link">
                       {this.props.contactData.location.values[0]}
                    </p>
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.props.contactData.location.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
        );
    }
});

var LocationEdit = React.createClass({    

    render: function() {
        return (
            <div className="location info-blog edit">
                <p className="info-title">location</p>
                <div className="">
                    <input type="text" className="title-reg" name="address" placeholder="street address" value={this.props.contactData.location.values[0]}/>
                    <input type="text" className="industry-reg" name="zip" placeholder="zip code" value={this.props.contactData.location.values[0]}/>
                    <input type="text" className="start-date-reg" name="state" placeholder="state" value={this.props.contactData.location.values[0]}/>
                    <div className="icon-wrapper">
                        <img className="icon edit-reg" src="images/edit.png" width="25" height="25" />
                    </div>
                </div>
            </div>
        );
    }
});

var Location = React.createClass({   
    render: function() {
        return (
            <div>
                <LocationMain contactData={this.props.contactData}/>
                <LocationEdit contactData={this.props.contactData} />
            </div>
        );
    }
});

var ContactCantainer = React.createClass({   

    render: function() {
        return (
            <div className="row info-blog-wrapper" >
                <div className="four columns" >
                    <Social data={this.props.contactData.social} />                
                </div> 
                 <div className="four columns" >
                    <Contact data={this.props.contactData.contact} />                
                </div>     
                <div className="four columns" >
                    <Location contactData={this.props.contactData}/>                
                </div> 
            </div>      
        );
    }
});

var ContactData = {
    "social": {
        "twitter":"@nben888",
        "facebook":"natebenson",
        "linkedin":"www.linkedin.com/natebenson",
        "visibility":"private"
    },
    "contact": {
        "email":"nathan@vyllage.com",
        "home":"555-890-2345",
        "cell":"555-123-2345",
        "visibility":"private"
    },
    "location": {
        "values":[
            "address line1",
            "address line2",
            "..."
        ],
        "visibility":"private"
    }
}

React.render(<ContactCantainer contactData={ContactData} />, document.getElementById('contact-info'));