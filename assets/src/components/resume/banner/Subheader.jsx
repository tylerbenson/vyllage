var React = require('react');
var Avatar = require('../../avatar');
var AddSection = require('../AddSection');
var FeatureToggle = require('../../util/FeatureToggle');
var ExportButton = require('../../export/ExportButton');
var ReorderButton = require('../../buttons/reorder');

var Subheader = React.createClass({
  shouldComponentUpdate: function(nextProps, nextState) {
    return nextProps !== this.props || nextState !== this.state;
  },
  render: function () {
  var name = this.props.name || '';

    return (
      <section className="subheader">
        <div className="content">
          <div className="options">
            <FeatureToggle name="PRINTING">
              <ExportButton documentId={this.props.ownDocumentId} sections={this.props.sections}  />
            </FeatureToggle>
            <ReorderButton />
          </div>

          <div className="add-container">
            <AddSection sections={this.props.sections} />
          </div>
        </div>
      </section>
    );
  }
});

module.exports = Subheader;