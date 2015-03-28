var React = require('react');

var Footer = React.createClass({  

  render: function() {
    return (
      <footer>
        <div className="content">
          <div className="logo">
            <img src="images/logo-orange.png" alt="Vyllage" />
            <span>Vyllage</span>
          </div>
          <nav>
            <ul>
              <li><a href="">Help</a></li>
              <li><a href="">Careers</a></li>
              <li><a href="">Terms</a></li>
              <li><a href="">Privacy</a></li>
              <li><a href="">Contact Us</a></li>
            </ul>
          </nav>
        </div>
      </footer>
    );
  }
});

React.render(<Footer />, document.getElementById('footer'));
module.exports = Footer;


