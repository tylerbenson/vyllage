var SocialMain = React.createClass({	

    render: function() {
		return (
            <div className="social info-blog">
                <p className="info-title">social</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn"></button>
                </div>
                <p className="address">twitter: {this.props.contactData.social.twitter}</p>
                <p className="info-link"><a href="www.vyllage.com/nathanbenson" className="soc-link">www.vyllage.com/nathanbenson</a>
                </p>
                <p className="info-privacy">visible to:
                    <select className="privacy-select">
                        <option>{this.props.contactData.social.visibility}</option>
                    </select>
                </p>
            </div>
		);
    }
});

var SocialEdit = React.createClass({    

    render: function() {
        return (
            <div className="social info-blog edit">
                <p className="info-title-reg">social</p>
                <div className="icon-wrapper-reg">
                    <img className="icon add" src="images/add.png" width="25" height="25" />
                </div>
                <select className="privacy-select">
                    <option>{this.props.contactData.social.twitter}</option>
                </select>
                <select className="privacy-select">
                    <option>{this.props.contactData.social.twitter}</option>
                </select>
            </div>
        );
    }
});

var Social = React.createClass({   
    render: function() {
        return (
            <div>
                <SocialMain contactData={this.props.contactData}/>
                <SocialEdit contactData={this.props.contactData} />
            </div>
        );
    }
});

var ContactMain = React.createClass({    

    render: function() {
        return (
            <div className="contact info-blog">
                <p className="info-title">contact</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn"></button>
                </div>
                <p className="address">email: <a href={this.props.contactData.contact.email}>{this.props.contactData.contact.email}</a> </p>
                <p className="address cell">cell: {this.props.contactData.contact.home}</p>
                <p className="info-privacy">visible to:
                    <select className="privacy-select">
                        <option>{this.props.contactData.contact.visibility}</option>
                    </select>
                </p>
            </div>
        );
    }
});


var ContactEdit = React.createClass({    

    render: function() {
        return (
            <div className="contact info-blog edit">
                <p className="info-title">contact</p>
                <div className="">
                    <input type="text" className="title-reg" name="email" placeholder="nathan@vyllage.com" value={this.props.contactData.contact.email} />
                    <input type="text" className="title-reg" name="phone" placeholder="phone number" value={this.props.contactData.contact.email}/>
                </div>
            </div>
        );
    }
});

var Contact = React.createClass({   
    render: function() {
        return (
            <div>
                <ContactMain contactData={this.props.contactData}/>
                <ContactEdit contactData={this.props.contactData}/>
            </div>
        );
    }
});

var LocationMain = React.createClass({    

    render: function() {
        return (
            <div className="location info-blog">
                <p className="info-title">location</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn"></button>
                </div>
                <p className="address">{this.props.contactData.location.values[0]}</p>
                <p className="address cell">Vancouver, WA 98686</p>
                <p className="info-privacy">visible to:
                    <select className="privacy-select">
                        <option>{this.props.contactData.location.visibility}</option>
                    </select>
                </p>
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
                    <Social contactData={this.props.contactData}/>                
                </div> 
                 <div className="four columns" >
                    <Contact contactData={this.props.contactData}/>                
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
        "twitter":"@nben888999",
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
};

React.render(<ContactCantainer contactData={ContactData} />, document.getElementById('contact-info'));