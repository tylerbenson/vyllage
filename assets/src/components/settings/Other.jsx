var React = require('react');

var Other = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              other
            </div>
            <div className="six columns">
              <a>add</a>
            </div>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="org name">Org name</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = Other;