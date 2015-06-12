var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');

var Print = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentWillMount: function () {
    actions.getResume();
  },
	render: function() {
		return (
			<a href={"/resume/"+this.state.resume.documentId+"/file/pdf"} className="flat print button">
        <i className="ion-printer"></i>
        Print
      </a>
		);
	}

});

module.exports = Print;