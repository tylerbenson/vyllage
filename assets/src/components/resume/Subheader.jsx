var React = require('react');

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
            <a href="javascript:window.print()" className="flat print button">
              <i className="ion-printer"></i>
              Print
            </a>
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