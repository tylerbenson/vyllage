var React = require('react');
var ContentEditable = require("react-contenteditable");
var Actions = require('./actions');

var FormSubject = React.createClass({
  handleChange: function(e){
    Actions.updateSubject(e.target.value);
  },
  render: function () {
    return (
      <div className='request-advice-form-subject'>
        <div className="one columns rqst-key-word">subject:</div>
        <div className="ten columns">
          <ContentEditable html={this.props.subject} onChange={this.handleChange}/>
        </div>
      </div>
    );
  }
});

module.exports = FormSubject;