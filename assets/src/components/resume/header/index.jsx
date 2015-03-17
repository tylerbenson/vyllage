var React = require('react');
var Headline = require('./Headline');
var ShareContactButtons = require('./share-contact-btns');
var Contact = require('../contact');
var Share = require('../share');
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
          <ShareContactButtons 
            toggleContact={this.toggleContact}
            toggleShare={this.toggleShare}
          />
        </article>
        <article className="info-sections" id='share-info'>
          <Share />
        </article>
        <article className="info-sections" id='contact-info'>
          <Contact />
        </article>
      </div>
    );
  }
});


module.exports = Header;

