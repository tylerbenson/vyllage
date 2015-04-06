var React = require('react');
var Actions = require('./actions');

var FormSubject = React.createClass({
  handleChange: function(e){
    Actions.updateSubject(e.target.value);
  },
  render: function () {
    return (
      <div className='subject content'>
        <h2>Subject:</h2>
        <input
          type="text"
          className="inline flat"
          defaultValue="Could you provide me some feedback on my resume?"
          onChange={this.handleChange}
        />
      </div>
    );
  }
});

module.exports = FormSubject;