var React = require('react');
var Reflux = require('reflux');
var filter = require('lodash.filter');
var sortby = require('lodash.sortby');
var classnames = require('classnames');

var MilestoneStore = require('./store');
var MilestoneActions = require('./actions');
var Avatar = require('../avatar');

var Milestone = React.createClass({
	mixins: [Reflux.connect(MilestoneStore)],
	componentDidMount: function () {
		//onclick outside
	  window.addEventListener('mousedown', this.toggleMilestonesPanel, false);
	},
	// componentWillUnmount: function () {
	//     window.removeListener('mousedown');
	// },
	toggleMilestonesPanel: function(e) {
		//added the keyword 'milestone-toggle' to panel toggles
		if(e.target.className.indexOf('milestone-toggle') > -1){
			MilestoneActions.toggle();
		}
		else {
			MilestoneActions.toggle(false);
		}
	},
	render: function() {
		if(!this.state.milestones) {
			return null;
		}

		var milestones = this.state.milestones;
		var numberOfTasksToShow = 3;

		var done = filter(milestones, {isDone: true});
		var percentage = (done.length / milestones.length) * 100;

		var key = 0;
		var tasksShown = 0;
		var NextSteps = sortby(milestones, "priority").map(function(milestone){
			var listClasses = classnames({
				'done' : milestone.isDone,
				'priority' : !milestone.isDone && tasksShown++ < 3
			});
			return (
				<li key={key++} className={listClasses}>
					<div className="icon">
						<i className={milestone.icon ? milestone.icon : 'ion-trophy'}></i>
					</div>
					<div className="text">{milestone.text}</div>
				</li>
			);
		});

		return (
			<a className="flat milestones button milestone-toggle">
				<Avatar className="milestone-toggle" src={this.state.avatar} size="18" borderWidth="0" />
				{percentage !== 100 ? <div className="milestone-toggle pin"></div> : null}
				<span className="milestone-toggle">Milestones</span>
				<div className={(this.state.isOpen ? 'visible ' : '') + 'panel'}>
					<div className="overview">
						<div className="avatar-container">
							<Avatar src={this.state.avatar} size="48" borderWidth="2" />
						</div>
						<div className="info">
							<div className="name">{this.state.name}</div>
							<div className="progress-bar">
								<div className="progress" style={{width: percentage + "%"}}></div>
								<div className="percentage">{percentage + "%"}</div>
							</div>
						</div>
					</div>
					<div className="title">What's Next?</div>
					<ul>
						{NextSteps}
					</ul>
				</div>
			</a>
		);
	}

});

module.exports = Milestone;