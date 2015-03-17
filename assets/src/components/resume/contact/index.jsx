var React = require('react');

var Social = React.createClass({
    render: function() {
		return (
            <div className="social info-blog">
                <p className="info-title">Social</p>
                <div className="edit-btn-cont u-pull-left" onClick={this.edit}>
                    <button className="edit-btn"></button>
                </div>
                <div className="contact-data-cont u-pull-left">
                    <p className="contact-link" ref="twitterMain">
                        twitter: <a href="" className="soc-link">
                             {this.state.data.twitter}
                        </a>
                    </p>
                    <EditField ref="twitterEdit" type={'twitter'} data={this.props.data.twitter} updateValue={this.updateValue}/>
                    <p className="contact-link" ref="linkedinMain"> 
                        linkedin: <a href="" className="soc-link">
                            {this.state.data.linkedin}
                        </a>
                    </p>
                    <EditField ref="linkedinEdit" type={'linkedin'} data={this.props.data.linkedin} updateValue={this.updateValue}/>
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.state.data.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
		);
    }
});

var EditField = React.createClass({    

    getInitialState: function() {
        return {value: this.props.data}; 
    },

    componentWillMount: function () {
        this.state.value = this.props.data;
    },

    handleChange: function(event) {
        this.setState({value: event.target.value});

        if (this.props.updateValue) {
            this.props.updateValue(this.props.type, event.target.value);
        }
    },

    render: function() {
        var value = this.state.value;

        return (
            <div className="contact-link edit">
                <input type="text" className="soc-link edit" value={value}  onChange={this.handleChange}/>
            </div>
        );
    }
});

var Contact = React.createClass({

    getInitialState: function() {
        return {data: '',
                editMode: false}; 
    },

    componentWillMount : function() {
        this.setState({data: this.props.data,
                editMode: false});
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

            if(this.props.updateContactData){
                this.props.updateContactData(this.state.data);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateValue: function (type, data) {
        if(type=="email"){
            this.state.data.email = data;
        } else if(type=="cell"){
            this.state.data.cell = data;
        }
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
                             {this.state.data.email}</a>
                    </p>
                    <EditField ref="emailEdit" type={'email'} data={this.props.data.email} updateValue={this.updateValue}/>
                    <p className="contact-link" ref="cellMain">
                        cell: <a href="" className="soc-link">
                            {this.state.data.cell}</a>
                    </p>
                    <EditField ref="cellEdit" type={'cell'}  data={this.props.data.cell} updateValue={this.updateValue}/>
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.state.data.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
        );
    }
});

var LocationMain = React.createClass({

     getInitialState: function() {
        return {data: '',
                editMode: false}; 
    },

    componentWillMount : function() {
        this.setState({data: this.props.data,
                editMode: false});
    },

    edit: function() {
        if(!this.state.editMode){
            this.refs.addressOneEdit.getDOMNode().style.display="block" ;
            this.refs.addressOneMain.getDOMNode().style.display="none" ;

            this.refs.addressTwoEdit.getDOMNode().style.display="block" ;
            this.refs.addressTwoMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.addressOneEdit.getDOMNode().style.display="none";
            this.refs.addressOneMain.getDOMNode().style.display="";

            this.refs.addressTwoEdit.getDOMNode().style.display="none" ;
            this.refs.addressTwoMain.getDOMNode().style.display="block" ;

            if(this.props.updateLocationData){
                this.props.updateLocationData(this.state.data);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateValue: function (type, data) {
        if(type=="addressOne"){
            this.state.data.values[0] = data;
        } else if(type=="addressTwo"){
            this.state.data.values[1] = data;
        }
    },

    render: function() {
        return (
            <div className="info-blog location-blog">
                <p className="info-title">Location</p>
                <div className="edit-btn-cont u-pull-left"  onClick={this.edit}>
                    <button className="edit-btn"></button>
                </div>
                <div className="contact-data-cont location-contact-data-cont u-pull-left">
                    <p className="contact-link" ref="addressOneMain">
                       {this.state.data.values[0]}
                    </p>
                    <EditField ref="addressOneEdit" type={'addressOne'}  data={this.state.data.values[0]} updateValue={this.updateValue}/>
                    
                    <p className="contact-link" ref="addressTwoMain">
                       {this.state.data.values[1]}
                    </p>
                    <EditField ref="addressTwoEdit" type={'addressTwo'}  data={this.state.data.values[1]} updateValue={this.updateValue}/>
                 
                    <p className="info-privacy">visible to:
                        <select className="privacy-select">
                            <option>{this.state.data.visibility}</option>
                        </select>
                    </p>
                </div>
            </div>
        );
    }
});

var ContactCantainer = React.createClass({

   getInitialState: function() {
        return {
            isMain: true,
            contactData: ''
        };
    },

   componentWillMount: function() {
        // ajax call will go here and fetch the whoole data
        this.setState({contactData: ContactData,
                        isMain: true});
    },

    updateSocialData: function(socialData){
        this.state.contactData.social = socialData;
        this.setState({contactData: this.state.contactData});
    },

    updateContactData: function(contactData){
        this.state.contactData.contact = contactData;
        this.setState({contactData: this.state.contactData});
    },

    updateLocationData: function(locationData){
        this.state.contactData.location = locationData;
        this.setState({contactData: this.state.contactData});
    },

    render: function() {
        return (
            <div className="row info-blog-wrapper" >
                <div className="four columns" >
                    <Social data={this.state.contactData.social} updateSocialData={this.updateSocialData}/>                
                </div> 
                 <div className="four columns" >
                    <Contact data={this.state.contactData.contact} updateContactData={this.updateContactData}/>                
                </div>     
                <div className="four columns" >
                    <LocationMain data={this.state.contactData.location} updateLocationData={this.updateLocationData}/>                
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
};

module.exports = ContactCantainer;