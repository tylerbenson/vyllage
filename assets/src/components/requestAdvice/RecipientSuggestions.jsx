var React = require('react');
var LayerMixin = require('react-layer-mixin');
var assign = require('lodash.assign');

var data = {
  "recent" : [
    {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
    {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
    {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
    {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
    {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"},
  ],
  "recommended": [
    {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
    {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
    {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
    {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
    {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"},
  ]
}

var Suggesions = React.createClass({
  mixins: [LayerMixin],
  getInitialState: function () {
    return { 
      isFocused: false,
      isOpen: false
    };
  },
  enterHandler: function (e) {
    this.setState({
      isFocused: true,
    });
  },
  leaveHandler: function (e) {
    this.setState({
      isFocused: false,
    });
  },
  clickHandler: function (recipient, e) {
    e.preventDefault();
    this.props.selectSuggestion(recipient);
    this.setState({
      isFocused: false,
    });
  },
  renderRecipientList: function (recipients) {
    return recipients.map(function (recipient, index) {
      return (
        <li key={index} className="item-line">
          <p className="item-text" onClick={this.clickHandler.bind(this, recipient)}>
            {recipient.firstName + " " + recipient.lastName}
          </p>
         </li>
      );
    }.bind(this));
  },
  renderLayer: function () {
    if (this.props.show || this.state.isFocused) {
      var style = {
        position: 'absolute'
      };
      style = assign({}, style, this.props.position);
      return (
        <div onMouseDown={this.enterHandler} onMouseUp={this.leaveHanlder}>
          <div id='suggested-users-list' style={style} className={"suggested-users-list"}>
            <ul className="">
              <li className="topper">
                <p className="topper-text">recent</p>
              </li>
              {this.renderRecipientList(data.recent)}
              <li className="topper">
                <p className="topper-text">recommended</p>
              </li>
              {this.renderRecipientList(data.recommended)}
            </ul>  
          </div>
        </div>
      );
    } else {
      return <div></div>;
    }
  },
  render: function () {
    return null;
  }
});

module.exports = Suggesions;
