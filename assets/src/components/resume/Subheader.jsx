var React = require('react');
var Print = require('./Print');

var Subheader = React.createClass({
  render: function () {
    return (
      <section className="subheader">
        <div className="content">
          <div className="mode">
            <i className="ion-document"></i>
            <span>Edit Mode</span>
          </div>
          <div className="pull right">
            {/*<button className="flat">
              <i className="ion-android-share-alt"></i>
              Share
            </button>
            */}
            <Print />
            <a href='/resume/get-feedback' className="button embossed">
              <i className="ion-person-stalker"></i>
              Get Feedback
            </a>
          </div>
        </div>
      </section>
    );
  }
});

module.exports = Subheader;