var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Permission = React.createClass({
  mixins: [SettingsMixin],
  linkedInHandler: function (e) {
    var re = new RegExp(/^https?:\/\//);
    var url = re.test(e.target.value) ? e.target.value: 'http://' + e.target.value
    this.changeHandler('linkedIn', {
      target: {
        value: url
      }
    })
  },
  render: function () {
    var settings = this.props.settings || [];
    // var urlSetting = filter(this.props.settings, {name: 'url'})[0] || {value: ''};
    var facebookSetting = filter(this.props.settings, {name: 'facebook'})[0] || {value: ''};
    var twitterSetting = filter(this.props.settings, {name: 'twitter'})[0] || {value: ''};
    var linkedInSetting = filter(this.props.settings, {name: 'linkedIn'})[0] || {value: ''};

    if (settings.length > 0) {
      return (
        <div className="content">     
          <div className="facebook-connect">
            
              <div className="right-part">
                 <button className='small inverted'>CONNECT</button>
              </div>

              <div className="left-part">
                Facebook
              </div>

              <br/>
            
              <input type="checkbox" />  Publish Vyllage updates on my timeline

          </div>               
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Permission;

