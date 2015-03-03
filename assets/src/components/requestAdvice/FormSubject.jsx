var React = require('react');
var ContentEditable = require("react-contenteditable");

var FormSubject = React.createClass({
  getInitialState: function(){
    return {html: '<p>Could you provide me some feedback on my resume?</p>'};
  },
  handleChange: function(e){
    this.setState({html: e.target.value});
  },
  render: function () {
    return (
      <div className='request-advice-form-subject'>
        <div className="one columns rqst-key-word">subject:</div>
        <div className="ten columns">
          <ContentEditable html={this.state.html} onChange={this.handleChange}/>
        </div>
      </div>
    );
  }
});

module.exports = FormSubject;