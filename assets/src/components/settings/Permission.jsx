var React = require('react');
var Reflux = require('reflux');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');
var Actions = require('./actions');
var SettingsStore = require('./store');

var Permission = React.createClass({
  mixins: [SettingsMixin , Reflux.connect(SettingsStore)],

  componentWillMount: function(){



  },

  render: function () {
    
    var settings = this.props.settings || [];
    var metatoken = document.getElementById('meta_token').content;

    if( this.props.facebook == true){
        var fbConnectButton = <button name="facebook-connect" type="submit" className='small inverted' value="connect">Connect</button>;
    }else{
        var fbConnectButton = <button name="facebook-disconnect" type="button" className='small' value="disconnect" onClick={this.disconnectFacebook}>Connected</button>;
    }



    if (settings.length > 0) {
      return (
        <div className="holder">     
          
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
  },

  disconnectFacebook : function(){
      Actions.makeFacebookDisconnect();
  }




});

module.exports = Permission;

