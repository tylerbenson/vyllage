var React = require('react');

var Email = React.createClass({
  render: function () {
    return (
      <li>
        <div className="eight columns">
          <div>
            <div className='six columns'>
              <span>email address:</span>
            </div>
            <div className="six columns">
              <a>change/add</a>
            </div>
          </div>
          <div>
            <div className="six columns">
              <span>nben888@gmail.com</span>
            </div>
            <div className="six columns">
              <a>primary</a>
            </div>
          </div>
        </div>
        <div className='four columns'>
          <select className="u-full-width">
            <option value="eveyone">Everyone</option>
          </select>
        </div>
      </li>
    );
  }
});

module.exports = Email;