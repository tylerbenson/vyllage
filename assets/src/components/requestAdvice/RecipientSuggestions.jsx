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
  renderRecipientList: function (recipients) {
    return recipients.map(function (recipient, index) {
      return <li className="item-line" onClick={this.props.selectSuggestion.bind(this, recipient)}><p className="item-text">{recipient.firstName + " " + recipient.lastName}</p></li>;
    }.bind(this));
  },
  renderLayer: function () {
    if (this.props.show) {
      var style = {
        position: 'absolute'
      };
      style = assign({}, style, this.props.position);
      return (
        <div style={style} className={"suggested-users-list"}>
          <ul className="">
            <li className="topper">
              <p className="topper-text">recent supporters</p>
            </li>
            {this.renderRecipientList(data.recent)}
            <li className="topper">
              <p className="topper-text">recommended supporters</p>
            </li>
            {this.renderRecipientList(data.recommended)}
          </ul>  
        </div>
      );
    } else {
      return null;
    }
  },
  render: function () {
    return null;
  }
});

module.exports = Suggesions;
