var React = require('react');
var Headline = require('./Headline');
var Tagline = require('./Tagline');
var ShareContactButtons = require('./share-contact-btns');
// var actions = require('../actions');

var Header = React.createClass({ 
  getInitialState: function () {
    return {
      showShare:false,
      showContact: false
    }
  },
  toggleShare: function (e) {
    e.preventDefault();
    this.setState({showShare: !this.state.showShare});
  },
  toggleContact: function (e) {
    e.preventDefault();
    this.setState({showContact: !this.state.showContact});
  },
  render: function() {
    var header = this.props.header || {};
    return (
      <div className="row">
        <article>
          <Headline {...header}/>
          <Tagline tagline={header.tagline} />
        </article>
      </div>
    );
  }
});

// <ShareContactButtons 
            // toggleContact={this.toggleContact}
            // toggleShare={this.toggleShare}
          // />
module.exports = Header;

