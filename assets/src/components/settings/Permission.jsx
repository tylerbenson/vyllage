var React = require('react');
var Reflux = require('reflux');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');
var Actions = require('./actions');
var SettingsStore = require('./store');

var Permission = React.createClass({
  mixins: [Reflux.connect(SettingsStore)],

  render: function () {

    var settings = this.props.settings || [];
    var metatoken = document.getElementById('meta_token').content;

    if( this.props.facebook == false){
        var fbConnectButton = <button name="facebook-connect" type="submit" className='small inverted' value="connect">Connect</button>;
    }else{
        var fbConnectButton = <button name="facebook-disconnect" type="button" className='small' value="disconnect" onClick={this.disconnectFacebook}>Connected</button>;
    }

 	if( this.props.google == false){
        var ggConnectButton = <button name="google-connect" type="submit" className='small inverted' value="connect">Connect</button>;
    }else{
        var ggConnectButton = <button name="google-disconnect" type="button" className='small' value="disconnect" onClick={this.disconnectGoogle}>Connected</button>;
    }

    if (settings.length > 0) {
      return (
        <div className="wrapper">
          <div className="content">
            <form action="/connect/facebook" method="POST">
              <div className="right-part">
                  {fbConnectButton}
              </div>
              <div className="left-part">
               <i className="ion-social-facebook icon-facebook"></i>
                Facebook
              </div>
               <input type="hidden" name="_csrf" value={metatoken} />
               <input type="hidden" name="scope" value="email,publish_actions" />
               {/*<input type="checkbox" className="social-checkbox" /> <span className="small-text"> Publish Vyllage updates on my timeline </span>*/}
            </form>
          </div>
          <div className="content">
             <form action="/connect/google" method="POST">
              <div className="right-part">
                  {ggConnectButton}
              </div>
              <div className="left-part">
                <i className="ion-social-google icon-google"></i>
                 Google
              </div>
               <input type="hidden" name="_csrf" value={metatoken} />
               <input type="hidden" name="scope" value="email profile" />
              </form>
          </div>
          { /*
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
         */ }


        </div>
      );
    } else {
      return null;
    }
  },

  connectWithFacebook : function() {
    console.log('connect with facebook');
  },
  
  connectWithGoogle: function(){
    console.log('connect with google');
  },

  connectWithTwitter: function(){
    console.log('connect with twitter');
  },

  connectWithLinkedIn: function(){
    console.log('connect with linkedIn');
  },

  disconnectFacebook : function(){
      Actions.makeFacebookDisconnect();
  },
  
  disconnectGoogle : function(){
      Actions.makeGoogleDisconnect();
  }




});

module.exports = Permission;

