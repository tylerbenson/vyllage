var React = require('react');

var ExportMenu = React.createClass({  

    render: function() {
      return (
        <div id="export-container" className="menu-container">
          <div id="export-menu"> 
            <ul className="export-menu">
              <li><i className="icon ion-document-text"></i>Word</li>
              <li onClick={this.account}><i className="icon ion-document"></i>PDF</li>
              <li onClick={this.signOut}><i className="icon ion-printer"></i>Print</li>
            </ul>
          </div>
        </div>
      );
    }
});

module.exports = ExportMenu;


