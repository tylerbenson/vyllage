var React = require('react');

var PhoneNumber = React.createClass({
  render: function () {
    return (
      <li>
        <div className="eight columns">
          <div>
            <div className="six columns">
              <span>phone number:</span>
            </div>
            <div className="six columns">
              <a>update</a>
            </div>
          </div>
          <div>
            <span>971.800.1565</span>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="orgname">Org name</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = PhoneNumber;