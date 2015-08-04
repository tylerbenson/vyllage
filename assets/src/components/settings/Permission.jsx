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
    var metatoken = document.getElementById('meta_token').content;

    if (settings.length > 0) {
      return (
        <div className="holder">     
          
          <div className="content">       
            <form action="/connect/facebook" method="POST">     
              <div className="right-part">
                 <button type="submit" className='small inverted'>CONNECT</button>
              </div>
              <div className="left-part">
               <i className="ion-social-facebook icon-facebook"></i>
                Facebook
              </div>    
               <input type="hidden" name="_csrf" value={metatoken} />
               <input type="hidden" name="scope" value="email,publish_actions" />
               <input type="checkbox" className="social-checkbox" /> <span className="small-text"> Publish Vyllage updates on my timeline </span>
            </form>
          </div>    
                 
          <div className="content">            
              <div className="right-part">
                 <button className='small inverted' onClick={this.connectWithTwitter}>CONNECT</button>
              </div>
              <div className="left-part">
                <i className="ion-social-twitter icon-twitter"></i>
                Twitter
              </div>
                         
              <input className="social-checkbox"   type="checkbox" /> <span className="small-text">Tweet Vyllage updates on my timeline</span>
          </div>    
                

          <div className="content">            
              <div className="right-part">
                 <button className='small inverted' onClick={this.connectWithLinkedIn}>CONNECT</button>
              </div>
              <div className="left-part">
                <i className="ion-social-linkedin icon-linkedIn"></i>
                LinkedIn
              </div>
                         
              <input className="social-checkbox"  type="checkbox" />  <span className="small-text">Post Vyllage updates on my timeline</span>
          </div> 


        </div>
      );
    } else {
      return null;
    }
  },

  connectWithFacebook : function() {
    console.log('connect with facebook');
  },

  connectWithTwitter: function(){
    console.log('connect with twitter');    
  },

  connectWithLinkedIn: function(){
    console.log('connect with linkedIn'); 
  }


});

module.exports = Permission;

