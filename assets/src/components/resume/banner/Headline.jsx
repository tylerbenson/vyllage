var React = require('react');

var Headline = React.createClass({
  getDefaultProps: function () {
    return {
      firstName: "",
      middleName: "",
      lastName: ""
    }
  },
  render: function() {
    return (
      <p className="headline">
        {this.props.firstName + " " + this.props.middleName + " " + this.props.lastName} 
      </p>  
    );
  }
});

module.exports = Headline;