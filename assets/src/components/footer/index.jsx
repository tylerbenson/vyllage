var React = require('react');
var SettingsActions = require('../settings/actions');
var Footer = React.createClass({

  componentDidMount: function () {

      var IDLE_TIMEOUT = 3600; //  60 min = 3600 sec
      var _idleSecondsCounter = 0;
      document.onclick = function() {
          _idleSecondsCounter = 0;
      };
      document.onmousemove = function() {
          _idleSecondsCounter = 0;
      };
      document.onkeypress = function() {
          _idleSecondsCounter = 0;
      };
      window.setInterval(CheckIdleTime, 600000); // 10 min = 600 sec = 60 * 10 * 1000 mil sec

      function CheckIdleTime() {
          _idleSecondsCounter++;
          if (_idleSecondsCounter <= IDLE_TIMEOUT) {
            SettingsActions.doPing();
          }
      }  
  },

  render: function() {
    return (
      <footer>
        <div className="content">
          <a href="/" className="logo">
            <img src="/images/logo-orange.png" alt="Vyllage" />
          </a>
          <nav>
            <ul>
              <li><a href="/careers">Careers</a></li>
              <li><a href="/privacy">Privacy</a></li>
              <li><a href="/contact">Contact Us</a></li>
            </ul>
          </nav>
        </div>
      </footer>
    );
  }
});

React.render(<Footer />, document.getElementById('footer'));
module.exports = Footer;


