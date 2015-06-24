var React = require('react');
var Reflux = require('reflux');
var Actions = require('../resume/actions');
var resumeStore = require('../resume/store');

var NavToggle = React.createClass({
	mixins: [Reflux.connect(resumeStore, 'resume')],
	handleClick: function(){
		Actions.toggleNav();
	},
	render: function() {
		var navClass = (this.state.resume.isNavOpen?'active ':'') + 'nav-toggle flat button';
		return (
			<span className={navClass} onClick={this.handleClick}><i className='ion-navicon-round'></i></span>
		);
	}

});

module.exports = NavToggle;