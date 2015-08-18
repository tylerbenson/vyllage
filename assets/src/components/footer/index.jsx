var React = require('react');

var Footer = React.createClass({

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


