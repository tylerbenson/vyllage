var React = require('react');

var FormMessage = React.createClass({
  render: function () {
    return (
      <div className='u-pull-left'>
        <p className="rqst-key-word">subject:</p>
        <div className="subject" contentEditable>
          <p className="subject-text">
            Could you provide me some feedback on my resume?
          </p>
        </div>
      </div>
    );
  }
});

module.exports = FormMessage;