var React = require('react');
var actions = require('../actions');

var Tagline = React.createClass({
  getInitialState: function () {
    return {
      edit: false,
      tagline: ''
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({tagline: nextProps.tagline});
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({
      edit: true
    });
  },
  handleChange: function (e) {
    this.setState({tagline: e.target.value});
  },
  keyPress: function(e) {
    if (e.key === 'Enter') {
      var tagline = this.state.tagline;
      actions.updateTagline(tagline);
      this.setState({
        edit: false
      })
    }
  },
  renderTaglineForm: function () {
    // var tagline = this.state.tagline || this.props.tagline;
    return (
        <div>
          <input type="text" className="tagline tagline-edit u-full-width"
              placeholder="add a professional tagline" 
              value = {this.state.tagline}
              onKeyPress={this.keyPress} 
              onChange={this.handleChange} />
        </div>
    );
  },
  renderTagline: function () {
    return (
      <div className="paragraph">
        <p className="tagline" onClick={this.editHandler}>{this.props.tagline}</p>
      </div>
    );
  },
  render: function() {
    return this.state.edit? this.renderTaglineForm(): this.renderTagline();
  }
});

module.exports = Tagline;