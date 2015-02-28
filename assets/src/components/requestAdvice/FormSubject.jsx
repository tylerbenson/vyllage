var React = require('react');

var FormSubject = React.createClass({
  render: function () {
    return (
      <div className='row'>
        <p className="two columns rqst-key-word">subject:</p>
        <div className="subject" >
          <p className="subject-text">
            Could you provide me some feedback on my resume?
          </p>
        </div>
      </div>
    );
  }
});

module.exports = FormSubject;