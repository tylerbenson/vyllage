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
            <button className="flat">
              <i className="ion-android-share-alt"></i>
              Share
            </button>
            <button className="flat">
              <i className="ion-document-text"></i>
              Export
            </button>
            <button className="embossed">
              <i className="ion-person-stalker"></i>
              Ask Advice
            </button>
          </div>
        </div>
      </section>
    );
  }
});

module.exports = Subheader;