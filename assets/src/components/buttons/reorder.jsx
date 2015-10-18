var React = require('react');
var Reflux = require('reflux');
var classnames = require('classnames');

var ResumeStore = require('../resume/store');
var ResumeActions = require('../resume/actions');

var Reorder = React.createClass({
	mixins: [Reflux.connect(ResumeStore, 'resume')],
	render: function() {
		var classes = classnames({
			'secondary': !this.state.resume.isSorting,
			'flat': true,
			'reorder': true
		});

		return (
			<button onClick={ResumeActions.toggleSorting} className={classes}>
        <i className="ion-arrow-swap"></i>
        <span>Reorder</span>
      </button>
		);
	}

});

module.exports = Reorder;