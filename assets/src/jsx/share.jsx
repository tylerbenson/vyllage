var PrivateLinkMain = React.createClass({	

    render: function() {
		return (
            <div className="private-link info-blog main-info">
                <p className="info-title">private link</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn"></button>
                </div>
                <p className="info-link">
                    <a href={this.props.shareData.privateLink.link}>{this.props.shareData.privateLink.link}</a>
                </p>
                <p className="info-privacy">expires after:
                    <select className="privacy-select">
                        <option>{this.props.shareData.privateLink.expiresAfter}</option>
                    </select>
                </p>
            </div>
		);
    }
});

var PrivateLinkEdit = React.createClass({    

    render: function() {
        return (
            <div className="private-link info-blog edit">
                <p className="info-title">private link</p>
                <div className="info-link">
                    <input type="text" className="info-input-edit" placeholder={this.props.shareData.privateLink.link} value= {this.props.shareData.privateLink.link} />
                </div>
                <p className="info-privacy-share">expires after:
                    <select className="privacy-select">
                        <option>{this.props.shareData.privateLink.expiresAfter}</option>
                    </select>
                </p>
            </div>
        );
    }
});

var PrivateLink = React.createClass({   
    render: function() {
        return (
            <div>
                <PrivateLinkMain shareData={this.props.shareData}/>
                <PrivateLinkEdit shareData={this.props.shareData} />
            </div>
        );
    }
});

var PublicLinkMain = React.createClass({    

    render: function() {
        return (
            <div className="public-link info-blog main-info">
                <p className="info-title">public link</p>
                <div className="edit-btn-cont">
                    <button className="edit-btn"></button>
                </div>
                <p className="info-link">
                    <a href={this.props.shareData.publicLink.link}>{this.props.shareData.publicLink.link}</a>
                </p>
                <p className="info-privacy">visible to:
                    <select className="privacy-select">
                        <option>{this.props.shareData.publicLink.visibleTo}</option>
                    </select>
                </p>
            </div>
        );
    }
});


var PublicLinkEdit = React.createClass({    

    render: function() {
        return (
            <div className="public-link info-blog edit">
                <p className="info-title">public link</p>
                <div className="info-link">
                    <input type="text" className="info-input-edit" name="email" placeholder="https://vyllage.com/nathanbenson123" value = {this.props.shareData.publicLink.link}/>
                </div>
                <p className="info-privacy-public">visible to:
                    <select className="privacy-select">
                        <option>{this.props.shareData.publicLink.visibleTo}</option>
                    </select>
                </p>
            </div>
        );
    }
});

var PublicLink = React.createClass({   
    render: function() {
        return (
            <div>
                <PublicLinkMain shareData={this.props.shareData}/>
                <PublicLinkEdit shareData={this.props.shareData}/>
            </div>
        );
    }
});


var LinkCantainer = React.createClass({   

    render: function() {
        return (
             <div className="row info-blog-wrapper">
                <div className="four columns" >
                    <PrivateLink shareData={this.props.shareData}/>                
                </div> 
                 <div className="four columns" >
                    <PublicLink shareData={this.props.shareData}/>                
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

React.render(<LinkCantainer shareData={ShareData} />, document.getElementById('share-info'));