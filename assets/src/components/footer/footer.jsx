var React = require('react');

var Footer = React.createClass({  

  render: function() {
    return (
      <footer>
        <div className="container">
            <div className="row">
              <div className="twelve columns">
                <div className="u-pull-left footer-logo"><p className="text">Vyllage</p> </div>
                <ul className="u-pull-right">
                  <li><a href="#">About</a></li>
                  <li><a href="#">FAQs</a></li>
                  <li><a href="#">Support</a></li>
                  <li><a href="#">Careers</a></li>
                  <li><a href="#">Terms</a></li>
                  <li><a href="#">Privacy</a></li>
                </ul>
              </div>
            </div>
          </div>
      </footer>
    );
  }
});

module.exports = Footer;


