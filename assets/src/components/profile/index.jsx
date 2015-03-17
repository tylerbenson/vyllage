var React = require('react');
var request = require('superagent');
var ArticleContent =  require('./article-content');

var ProfileContainer = React.createClass({ 

		getInitialState: function() {
				return {profileData: []};
		},

		componentDidMount: function() {

				// var self = this, documentId,
				// 		pathItems = window.location.pathname.split("/");

				// if(pathItems.length > 2) {
				// 		documentId = pathItems[2];

				// 		request
				// 			 .get('/resume/' + documentId + '/header')
				// 			 .set('Accept', 'application/json')
				// 			 .end(function(error, res) {

				// 						if (res.ok) {
				// 								if(res.body.length == 0) {
				// 										// if data from server is empty TBD??
				// 								} else {
				// 										self.setState({profileData: res.body});
				// 								}
				// 						} else {
				// 								alert(res.text); // this is left intentionally 
				// 								console.log(res.text);
				// 					 }             
				// 		});
				// }
		},

		saveChanges: function (data) {
						var self = this, documentId,
						pathItems = window.location.pathname.split("/"),
						token_header = document.getElementById('meta_header').content,
						token_val = document.getElementById('meta_token').content;

				if(pathItems.length > 2) {
						documentId = pathItems[2];

						request
								.post('/resume/' + documentId + '/header')
								.set(token_header, token_val)
								.send(data)
								.end(function(error, res) {

										if (res.ok) {
												 self.setState({profileData: data});
										} else {
											 alert( res.text );  // this is left intentionally
											 console.log(res.text); 
										}  
								});
				}
		},

		render: function() {
				return (
					<ArticleContent profileData={this.state.profileData} saveChanges={this.saveChanges}/>
				);
		}
});

// This render to id should be deleted after refractoring
if (document.getElementById('resume-contactInfo')) {
		React.render(<ProfileContainer />, document.getElementById('resume-contactInfo'));
}

module.exports = ProfileContainer;

