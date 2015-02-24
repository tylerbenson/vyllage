var React = require('react');
var RecipientAdd = require('./RecipientAdd');
var RecipientList = require('./RecipientList');

var FormTo = React.createClass({
  render: function () {
    return (
        <div className='content-part'>
          <p className="rqst-key-word one column">to:</p>
          <div className='nine columns'>
             <RecipientAdd />
             <RecipientList />
          </div>
          <div className="two columns fb-button">
            <p className="small-text">ask your</p>
            <p className="big-text">facebook</p>
            <p className="small-text">friends</p>
          </div>
        </div>
    );
  }
});


module.exports = FormTo;