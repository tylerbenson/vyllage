var React = require('react');

var SecondaryHeader = React.createClass({  

    render: function() {
      return (
          <div className="header-secondary">
            <div className="container">
              <div className="row">
                <div className="twelve columns">
                  <div className="u-pull-left mode">Edit Mode</div>
                  <div className="u-pull-right">
                    <button className="u-pull-left ask-advice-btn" onClick={this.askAdvise}>ask advice</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
      );
    }
});

module.exports = SecondaryHeader;


