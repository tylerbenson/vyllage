var React = require('react');

var OrganizationEdit = React.createClass({
  getInitialState: function() {
    return { organization:this.props.section }; 
  },
  handleChange: function(key, e) {
    e.preventDefault();
    var organization
    this.setState({description: e.target.value});
  },
  saveHandler: function(e) {
    var section = this.props.section;
    section.description = this.state.description;
    section.uiEditMode = false;
    actions.putSection(section);
  },
  cancelHandler: function(e) {
    e.preventDefault();
    this.setState({description:this.props.section.description});
    actions.disableEditMode(this.props.section.sectionId);
  },
  render: function () {
    return (
      <div></div>
    );
  }
});

module.exports = OrganizationEdit;