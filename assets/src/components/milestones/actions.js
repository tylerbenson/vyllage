var Reflux = require('reflux');

var MilestoneActions = Reflux.createActions([
	'syncMilestones', //For manually syncing milestones
	'toggle',
	'viewAllToggle',
]);

module.exports = MilestoneActions;