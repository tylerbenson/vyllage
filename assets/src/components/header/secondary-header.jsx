var React = require('react');

var SecondaryHeader = React.createClass({  

    exportMenu: function() {
      if(this.props.exportMenu) {
        this.props.exportMenu();
      }
    },

    share: function() {

    },
    
    askAdvise: function() {
      var pathItems = window.location.pathname.split("/"),
          documentId;
      if(pathItems.length > 2) {
        documentId = pathItems[2];
      }
      if(documentId) {
        window.location.pathname = "resume/"+ documentId + "/ask-advice";
      }
    },

    render: function() {
      return (
        <div className="header-secondary">
          <div className="container">
            <div className="row">
              <div className="twelve columns">
                <p className="u-pull-left mode"> 
                  <i className="icon ion-document-text"></i>
                    Edit Mode  
                  <span className="arrow-down"> </span>
                </p>  
                <div className="u-pull-right">
                  <a className="u-pull-left share" onClick={this.share}> 
                    <i className="icon ion-android-share-alt"></i>  share
                  </a>  
                  <a className="u-pull-left export">
                    <i className="icon ion-printer"></i>  Print 
                  </a>
                 <button className="u-pull-left ask-advice-btn" onClick={this.askAdvise}> 
                    <i className="icon ion-person-stalker"></i>  ask advice
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    }
});

module.exports = SecondaryHeader;


