var sections = [
	{
		title: 'Objective',
		type: 'SummarySection',
		component: 'Freeform',
		icon: 'ion-android-list',
		description: 'Create a profile overview with a summary of your goal for your next career move.',
		isMultiple: false
	},
	{
		title: 'Job Experience',
		type: 'JobExperienceSection',
		component: 'Organization',
		icon: 'ion-briefcase',
		description: 'Include experiences relevant to your field.',
		isMultiple: true
	},
	{
		title: 'Education',
		type: 'EducationSection',
		component: 'Organization',
		icon: 'ion-university',
		description: 'Add the level of education you have attained so far.',
		isMultiple: true
	},
	{
		title: 'Skills',
		type: 'SkillsSection',
		component: 'Tags',
		icon: 'ion-hammer',
		description: 'List skills that you have been developing.',
		isMultiple: false
	},
	{
		title: 'Projects',
		type: 'ProjectsSection',
		component: 'Project',
		icon: 'ion-folder',
		description: 'Showcase your skills and talent by including projects you\'ve worked on.',
		isMultiple: true
	},
	// {
	// 	title: 'Achievements',
	// 	type: 'AchievementsSection',
	// 	component: 'Gallery',
	// 	icon: 'ion-folder',
	// 	description: 'List awards that lets you stand out from the rest.',
	// 	isMultiple: false
	// },
	{
		title: 'Career Interests',
		type: 'CareerInterestsSection',
		component: 'Tags',
		icon: 'ion-android-star',
		description: 'List jobs or degrees you\'re looking forward to pursue',
		isMultiple: false
	}
	// {
	// 	title: 'Personal References',
	// 	type: 'PersonalReferencesSection',
	// 	component: 'References',
	// 	icon: 'ion-ios-people',
	// 	description: 'Add your peers who know you personally.',
	// 	isMultiple: false
	// },
	// {
	// 	title: 'Professional References',
	// 	type: 'ProfessionalReferencesSection',
	// 	component: 'References',
	// 	icon: 'ion-coffee',
	// 	description: 'Add colleagues who can testify your work ethic.',
	// 	isMultiple: false
	// }
];

module.exports = sections;