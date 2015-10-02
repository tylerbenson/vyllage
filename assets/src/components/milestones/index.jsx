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
	viewAllToggle: function() {
		MilestoneActions.viewAllToggle();
	},
	toggleMilestonesPanel: function(e) {
		if(e.target.className.indexOf('milestone-ignore') > -1){
			return;
		}
		//added the keyword 'milestone-toggle' to panel toggles
		if(e.target.className.indexOf('milestone-toggle') > -1){
			MilestoneActions.toggle();
		}
		else{
			MilestoneActions.toggle(false);
		}

		MilestoneActions.viewAllToggle(false);
	},
	render: function() {
		if(!this.state.milestones) {
			return null;
		}

		var milestones = this.state.milestones;
		var numberOfTasksToShow = 3;

		var done = filter(milestones, {isDone: true});
		var percentage = ((done.length / milestones.length) * 100).toFixed(2);

		var key = 0;
		var tasksShown = 0;
		var incompleteTasks = 0;
		var NextSteps = sortby(milestones, "priority").map(function(milestone){
			if(!milestone.isDone){
				incompleteTasks++;
			}

			var listClasses = classnames({
				'done' : milestone.isDone,
				'priority' : !milestone.isDone && tasksShown++ < 3
			});

			return (
				<li onMouseDown={milestone.action} key={key++} className={listClasses}>
					<div className="icon">
						<i className={milestone.icon ? milestone.icon : 'ion-trophy'}></i>
					</div>
					<div className="text">{milestone.text}</div>
				</li>
			);
		});

		var TasksComplete = (
			<div className="completed">
				<div className="message">
					Well done for completing your Vyllage Resumé!
					Keep sharing to get some valuable feedback.
				</div>
			</div>
		);

		var Toggle = <button onMouseDown={this.viewAllToggle} className="milestone-ignore toggle flat">{this.state.viewAll ? 'View Priority' : 'View All'}</button>

		return (
			<a className="flat milestones button milestone-toggle">
				<Avatar className="milestone-toggle" src={this.state.avatar} size="18" borderWidth="0">
					{percentage !== 100 ? <div className="milestone-toggle pin"></div> : null}
				</Avatar>
				<span className="milestone-toggle">Milestones</span>

				<div className={(this.state.isOpen ? 'visible ' : '') + 'overlay'}>
					<div className='panel' ref='panel'>
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
						<div className="title">{ incompleteTasks > 0 ? "Next Steps" : "Tasks Complete!" }</div>
						<ul className={this.state.viewAll?'view-all':''}>
							{
								incompleteTasks > 0 ?
								NextSteps :
								TasksComplete
							}
						</ul>
						{incompleteTasks > numberOfTasksToShow ? Toggle : null}
					</div>
				</div>
			</a>
		);
	}

});

module.exports = Milestone;