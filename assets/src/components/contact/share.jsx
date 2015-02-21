var React = require('react');

var ShareData = {
    "privateLink": {
        "link":"www.linkedin.com/natebenson",
        "expiresAfter":"40 days"
    },
    "publicLink": {
        "link":"www.linkedin.com/natebenson",
        "visibleTo":"public"
    }
};

var PrivateLinkEdit = React.createClass({

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
            <div className="share-link edit">
                <input type="text" className="info-input-edit" value={link}  onChange={this.handleChange}/>
            </div>
        );
    }
});

var PrivateLink = React.createClass({

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    editPrivateLink: function() {
        if(!this.state.editMode){
            this.refs.privateLinkEdit.getDOMNode().style.display="block" ;
            this.refs.privateLinkMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.privateLinkEdit.getDOMNode().style.display="none";
            this.refs.privateLinkMain.getDOMNode().style.display="block";

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updatePrivateLink: function (link) {
        this.state.data.link=link;
    },

    render: function() {
		return (
            <div className="private-link info-blog main-info">
                <p className="info-title">private link</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn" onClick={this.editPrivateLink}></button>
                </div>
                <p className="share-link" ref="privateLinkMain">
                    <a href={this.props.data.link}>{this.props.data.link}</a>
                </p>
                <PrivateLinkEdit ref="privateLinkEdit" data={this.props.data} updatePrivateLink={this.updatePrivateLink}/>
                <p className="info-privacy">expires after:
                    <select className="privacy-select">
                        <option>{this.props.data.expiresAfter}</option>
                    </select>
                </p>
            </div>
		);
    }
});

var PublicLinkEdit = React.createClass({

    getInitialState: function() {
        return {link: this.props.data.link}; 
    },

    componentDidUpdate: function () {
        this.state.link = this.props.data.link;
    },

    handleChange: function(event) {
        this.setState({link: event.target.value});

        if (this.props.updatePublicLink) {
            this.props.updatePublicLink(event.target.value);
        }
    },

    render: function() {
        var link = this.state.link;

        return (
            <div className="share-link edit">
                <input type="text" className="info-input-edit" value={link} onChange={this.handleChange} />
            </div>
        );
    }
});

var PublicLink = React.createClass({  

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },  

    editPublicLink: function() {
        if(!this.state.editMode) {
            this.refs.publicLinkEdit.getDOMNode().style.display="block";
            this.refs.publicLinkMain.getDOMNode().style.display="none" ;
         } else {
            this.refs.publicLinkEdit.getDOMNode().style.display="none";
            this.refs.publicLinkMain.getDOMNode().style.display="block" ;

            if(this.props.updatePublicLink) {
                this.props.updatePublicLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updatePublicLink: function (link) {
       this.state.data.link=link;
    },

    render: function() {
        return (
            <div className="public-link info-blog main-info">
                <p className="info-title">public link</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn" onClick={this.editPublicLink}></button>
                </div>
                <p className="share-link" ref="publicLinkMain">
                    <a href={this.props.data.link}>{this.props.data.link}</a>
                </p>

                <PublicLinkEdit ref="publicLinkEdit" data={this.props.data} updatePublicLink={this.updatePublicLink}/>
                <p className="info-privacy">visible to:
                    <select className="privacy-select">
                        <option>{this.props.data.visibleTo}</option>
                    </select>
                </p>
            </div>
        );
    }
});

var LinkContainer = React.createClass({   

    getInitialState: function() {
        return {ShareData: ShareData};
    },

    componentDidMount: function() {
        // ajax call will go here and fetch the profileData

        this.setState({ShareData: ShareData});
    },

    updatePrivateLink: function (link) {
        this.state.ShareData.privateLink.link=link;
        this.setState({ShareData:  this.state.ShareData});
    },

    updatePublicLink: function (link) {
        this.state.ShareData.publicLink.link=link;
        this.setState({ShareData:  this.state.ShareData});
    },

    render: function() {
        return (
             <div className="row info-blog-wrapper">
                <div className="four columns" >
                    <PrivateLink data={this.state.ShareData && this.state.ShareData.privateLink} updatePrivateLink={this.updatePrivateLink}/>                
                </div> 
                 <div className="four columns" >
                    <PublicLink data={this.state.ShareData && this.state.ShareData.publicLink} updatePublicLink={this.updatePublicLink} />                
                </div>      
                <div className="four columns">
                    <div className="export info-blog">
                        <p className="info-title">export</p>
                        <div className="exp-buttons">
                            <div className="exp-button">word</div>
                            <div className="exp-button">PDF</div>
                            <div className="exp-button">print</div>
                        </div>
                    </div>
                </div>
            </div>      
        );
    }
});



React.render(<LinkContainer ShareData={ShareData} />, document.getElementById('share-info'));

module.exports = LinkContainer;