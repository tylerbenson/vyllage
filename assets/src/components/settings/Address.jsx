var React = require('react');

var Address = React.createClass({
  render: function () {
    return (
      <li>
        <div className="eight columns">
          <div>
            <div className="six columns">
              <div>address:</div>
              <div>1906 NE 151st Cir</div>
              <div>Unit 15b</div>
              <div>Vancovuer, WA 98686</div>
            </div>
            <div className="six columns">
              <a>update</a>
            </div>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="no one">No One</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = Address;