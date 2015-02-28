var React = require('react');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Body = require('./FormBody');

var Form = React.createClass({
  render: function () {
    return (
      <div className='row'>
        <div className='twelve columns'>
          <form>
            <To />
            <Subject />
            <div className='message'>
              <Body />
              <div className='u-pull-right'>
                <button className="cancel-btn">cancel</button>
                <button className="send-btn">send</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    );
  }
});

module.exports = Form;