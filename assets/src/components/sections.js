var sections = [
	{
		name: 'Summary',
		type: 'SummarySection',
		component: 'Freeform',
		icon: 'ion-android-list',
		description: 'Create a profile overview with your career objectives.',
		isMultiple: false
	},
	{
		name: 'Job Experience',
		type: 'JobExperienceSection',
		component: 'Organization',
		icon: 'ion-briefcase',
		description: 'Include experiences relevant to your field.',
		isMultiple: true
	},
	{
		name: 'Education',
		type: 'EducationSection',
		component: 'Organization',
		icon: 'ion-university',
		description: 'Add the level of education you have attained so far.',
		isMultiple: true
	},
	{
		name: 'Skills',
		type: 'SkillsSection',
		component: 'Tags',
		icon: 'ion-hammer',
		description: 'List skills that you have been practicing for work.',
		isMultiple: false
	},
	// {
	// 	name: 'Projects',
	// 	type: 'ProjectsSection',
	// 	component: 'Gallery',
	// 	icon: 'ion-folder',
	// 	description: 'Showcase your work by including projects.',
	// 	isMultiple: false
	// },
	// {
	// 	name: 'Achievements',
	// 	type: 'AchievementsSection',
	// 	component: 'Gallery',
	// 	icon: 'ion-folder',
	// 	description: 'List awards that lets you stand out from the rest.',
	// 	isMultiple: false
	// },
	{
		name: 'Career Interests',
		type: 'CareerInterestsSection',
		component: 'Tags',
		icon: 'ion-android-star',
		description: 'List jobs or degrees you\'re looking forward to pursue',
		isMultiple: false
	}
	// {
	// 	name: 'Personal References',
	// 	type: 'PersonalReferencesSection',
	// 	component: 'References',
	// 	icon: 'ion-ios-people',
	// 	description: 'Add your peers who know you personally.',
	// 	isMultiple: false
	// },
	// {
	// 	name: 'Professional References',
	// 	type: 'ProfessionalReferencesSection',
	// 	component: 'References',
	// 	icon: 'ion-coffee',
	// 	description: 'Add colleagues who can testify your work ethic.',
	// 	isMultiple: false
	// }
];

module.exports = sections;