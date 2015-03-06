var React = require('react');

var Linkedin = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              linkedin
            </div>
            <div className="six columns">
              <a>remove</a>
            </div>
          </div>
          <div>
            <span>www.linkedin.com/natebenson</span>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="no one">no one</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = Linkedin;