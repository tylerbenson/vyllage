var React = require('react');

var Twitter = React.createClass({
  render: function () {
    return (
      <li>
        <div className="eight columns">
          <div>
            <div className="six columns">
              twitter:
            </div>
            <div className="six columns">
              <a>update</a>
            </div>
          </div>
          <div>
            <span>@natespn</span>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="everyone">EveryOne</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = Twitter;