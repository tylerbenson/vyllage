var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var Actions = require('./actions');

var OtherList = React.createClass({
  render: function () {
    console.log(this.props.others);
    var otherNodes = this.props.others.map(function (other, index) {
      return <li key={index} className='row settings-account-item'>
              <div className="eight columns">
                <div>
                  <div className="six columns">
                    {other.value}
                  </div>
                  <div className="six columns">
                    <a>update/remove</a>
                  </div>
                </div>
              </div>
              <div className='four columns'>
                <PrivacySelect 
                  value={other.privacy}
                  organization={this.props.organization}
                  onChange={this.privacyHandler} /> 
              </div>
            </li>;
    }.bind(this)); 
    return (
      <ul>
        {otherNodes}
      </ul>
    );
  }
});

module.exports = OtherList;