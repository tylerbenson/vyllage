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
            {/*
            <a href="javascript:window.print()" className="flat button">
              <i className="ion-document-text"></i>
              Export
            </a>
            */}
            <a href={'/resume/' + this.props.documentId + '/ask-advice'} className="button embossed">
              <i className="ion-person-stalker"></i>
              Ask Advice
            </a>
          </div>
        </div>
      </section>
    );
  }
});

module.exports = Subheader;