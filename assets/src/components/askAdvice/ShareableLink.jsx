var React = require('react');
var Clipboard = require('react-zeroclipboard');

var ShareableLink = React.createClass({
	render: function(){
		return (
			<div className="sections">
				<div className="section shareable">
					<div className="container">
						<div className="content">
							<p className="tip">Anyone with the link below will be able to
							<br /> comment on your resume after signing up.</p>
							<Clipboard text="Hi, world!">
								<input id="shareable-link" className="padded" type="text" />
								<button className="padded"><i className="ion-android-clipboard"></i> Copy</button>
	            </Clipboard>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

module.exports = ShareableLink;