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
    var selectSuggestion = this.props.selectSuggestion;
    return recipients.map(function (recipient, index) {
      return (
        <li 
          key={index}
          className="item-line"
          onClick={selectSuggestion(recipient)}>
          <p className="item-text">{recipient.firstName + " " + recipient.lastName}</p>
         </li>
      );
    });
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
              <p className="topper-text">recent</p>
            </li>
            {this.renderRecipientList(data.recent)}
            <li className="topper">
              <p className="topper-text">recommended</p>
            </li>
            {this.renderRecipientList(data.recommended)}
          </ul>  
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
