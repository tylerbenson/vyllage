var React = require('react');

var Facebook = React.createClass({
  render: function () {
    return (
      <li>
        <div className="eight columns">
          <div>
            <div className="six columns">
              facebook.com
            </div>
            <div className="six columns">
              <a>reset all</a>
            </div>
          </div>
          <div>
            <span>www.facebook.com/natebenson</span>
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

module.exports = Facebook;