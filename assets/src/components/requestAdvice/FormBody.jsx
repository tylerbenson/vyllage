var React = require('react');
var ContentEditable = require("./ContentEditable");

var defaultBody = '';
defaultBody += '<p> I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me? </p>';
defaultBody += '<br/><p>Thanks,</p><p>Nathan</p>';

var FormBody = React.createClass({
  getInitialState: function(){
    return {html: defaultBody};
  },
  handleChange: function(e){
    this.setState({html: e.target.value});
  },
  render: function () {
    return (
      <div className = "messageContent">
        <ContentEditable html={this.state.html} onChange={this.handleChange}/>
      </div>
    );
  }
});

module.exports = FormBody;

